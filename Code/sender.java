/*
	Program for Slotted Aloha Simulation

	Written by Rahul Kejriwal
*/

import java.util.*;
import java.io.*;
import java.lang.*;

import java.util.concurrent.ConcurrentLinkedQueue;

/*
	Class for modelling a single node in a Slotted Aloha MAC based framework
*/
class Node{

	// Node Parameters
	int cw;
	boolean mach_friendly;
	int max_retry_attempts;
	
	// Internal State Vars
	int buffer_count;
	int backoff_ctr;
	int pkt_delay_1;
	int pkt_delay_2;
	int retry_attempts;
	boolean backlogged;

	// Construct new node
	Node(int def_cw, int mra, boolean mf){
		
		// Initialize node params
		cw = def_cw;
		mach_friendly = mf;
		max_retry_attempts = mra;

		// Initialize internal state vars
		buffer_count = 0;
		backoff_ctr = 0;
		pkt_delay_1 = 0;
		pkt_delay_2 = 0;
		retry_attempts = 0;
		backlogged = false;
	}

	// Generate new pkt
	void gen_pkt(){
		if(buffer_count == 2)
			return;

		buffer_count ++;
		backlogged = true;		
	}

	/*
		Returns 1 if wants to xmit
		Returns 0 for pass
	*/
	int slot_action(){
		if(!backlogged)
			return 0;

		pkt_delay_1 ++;
		if(buffer_count == 2)
			pkt_delay_2 ++;

		if(backoff_ctr == 0)
			return 1;
		else{
			backoff_ctr --;
			return 0;
		}
	}

	// Update state with Succesful Xmission
	int update_success(){
		int delay = pkt_delay_1; 
		cw = (int) Math.max(2, cw * 0.75);

		buffer_count --;
		if(buffer_count == 0)
			backlogged = false;
		else
			backlogged = true;
		
		if(buffer_count == 1){
			pkt_delay_1 = pkt_delay_2;
			pkt_delay_2 = 0;
		}
		else{
			pkt_delay_1 = 0;
			pkt_delay_2 = 0;
		}
		retry_attempts = 0;
		backoff_ctr = 0;
		
		return delay;
	}

	// Update state with Failed Xmission
	boolean update_fail(){
		backoff_ctr = new Random().nextInt(cw);
		cw = Math.min(256, cw*2);
		
		if(retry_attempts == max_retry_attempts){
			if(!mach_friendly)
				System.out.println("Exceeded 10 retries for node!");
			return true;
		}

		retry_attempts ++;
		return false;
	}
}

/*
	Class for simulating Slotted Aloha for N users
*/
class SlottedAlohaSender{
	
	// Simulation Params 
	int num_users;
	int def_cw;
	double pkt_gen_rate;
	int max_pkts;

	// Xmitter Nodes
	Node[] nodes;

	// Print machine friendly?
	boolean mach_friendly;

	SlottedAlohaSender(int n, int dcw, double pgr, int mp, int mra, boolean mf){
		num_users = n;
		def_cw	  = dcw;
		pkt_gen_rate = pgr;
		max_pkts     = mp;

		mach_friendly = mf;

		nodes = new Node[num_users];
		for(int i=0; i < num_users; i++)
			nodes[i] = new Node(def_cw, mra, mach_friendly);

		run_simulation();
	}

	boolean genBoolWithProb(){
		return Math.random() < pkt_gen_rate;
	}

	void run_simulation(){
		int SimTime = 0;
		int num_pkts_xmitted = 0;
		int total_delay_time = 0;

		while(num_pkts_xmitted < max_pkts){

			// Advance Simulation Time
			SimTime ++;

			// Packet Generation Phase
			for(Node node: nodes){
				if(genBoolWithProb())
					node.gen_pkt();
			}

			// Attempt Transmission Phase
			int num_attempted = 0;

			// Do Slot Actions
			ArrayList<Node> attemptors = new ArrayList<Node>();
			for(Node node: nodes){
				if(node.slot_action() == 1)
					attemptors.add(node);
			}

			// Check for collisions and update accordingly
			if(attemptors.size() == 1){
				num_pkts_xmitted ++;
				total_delay_time += attemptors.get(0).update_success();
			}
			else{
				for(Node node: attemptors)
					if(node.update_fail()){
						// Print human-readable
						if(!mach_friendly){
							System.out.println("\nQuitting after " + Integer.toString(num_pkts_xmitted) + " pkts\n");
							System.out.println("N: " + Integer.toString(num_users) 
								+ ", W: " + Integer.toString(def_cw)
								+ ", P: " + Double.toString(pkt_gen_rate)
								+ ", Utilization: " + Double.toString( (num_pkts_xmitted*1.0)/SimTime )
								+ ", Average Packet Delay: " + Double.toString( (total_delay_time*1.0)/num_pkts_xmitted ));				
						}
						// Print machine-friendly
						else{
							System.out.println(num_pkts_xmitted);
							System.out.println(num_users);
							System.out.println(def_cw);
							System.out.println(pkt_gen_rate);
							System.out.println((num_pkts_xmitted*1.0)/SimTime);
							System.out.println((total_delay_time*1.0)/num_pkts_xmitted);
						}
						System.exit(0);							
					}
			}
		}

		// Print human-readable
		if(!mach_friendly){
			System.out.println("N: " + Integer.toString(num_users) 
				+ ", W: " + Integer.toString(def_cw)
				+ ", P: " + Double.toString(pkt_gen_rate)
				+ ", Utilization: " + Double.toString( (num_pkts_xmitted*1.0)/SimTime )
				+ ", Average Packet Delay: " + Double.toString( (total_delay_time*1.0)/num_pkts_xmitted ));				
		}
		// Print machine-friendly
		else{
			System.out.println(-1);
			System.out.println(num_users);
			System.out.println(def_cw);
			System.out.println(pkt_gen_rate);
			System.out.println((num_pkts_xmitted*1.0)/SimTime);
			System.out.println((total_delay_time*1.0)/num_pkts_xmitted);
		}
		System.exit(0);
	}
}

/*
	Main Class:
		Parse cmd line args and start simulator
*/
public class sender{
	static void errorExit(String s){
		System.out.println("Error: " + s);
		System.exit(1);
	}

	public static void main(String[] args){
		// Protocol Params
		int num_users 	 = 24;  
		int def_cw		 = 2;
		double pkt_gen_rate = 0.5;
		int max_pkts	 = 400;

		int max_retry_attempts = 10;

		// machine friendly op modes
		boolean mach_friendly = false;

		// Process Command Line Args
		int next_arg = 0;
		for(String arg: args){
			if(next_arg == 0){
				if(arg.equals("-N"))
					next_arg = 3;
				else if(arg.equals("-W"))
					next_arg = 4;
				else if(arg.equals("-p"))
					next_arg = 5;
				else if(arg.equals("-M"))
					next_arg = 6;
				else if(arg.equals("-r"))
					next_arg = 7;
				else if(arg.equals("-m"))
					mach_friendly = true;
				else
					errorExit("Incorrect Usage!");
			}
			else{
				switch(next_arg){			
					case 3: num_users = Integer.parseInt(arg);
							break;

					case 4: def_cw = Integer.parseInt(arg);
							break;

					case 5: pkt_gen_rate = Double.parseDouble(arg);
							break;

					case 6: max_pkts = Integer.parseInt(arg);
							break;

					case 7: max_retry_attempts = Integer.parseInt(arg);
							break;

					default: errorExit("Incorrect Usage!");
				}
				next_arg = 0;
			}
		}

		// Start simulator
		SlottedAlohaSender s = new SlottedAlohaSender(num_users, def_cw, pkt_gen_rate, max_pkts, max_retry_attempts, mach_friendly);
	}
}
