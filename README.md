# Slotted-Aloha-Simulator

A Java-based simulation of Slotted Aloha MAC Protocol

Usage:

1. Run ```make``` to build the executables.
2. Change into the Code directory using ```cd Code```.
3. Run the program using ```java sender [options]``` 
    
    where options can be:
    
        -N <num_users>              default value = 24
    
        -W <def_collision_window>   default value = 2
    
        -p <pkt_gen_rate>           default value = 0.5
    
        -M <max_pkts>               default value = 400
    
        -r <max_retry_attempts>     default value = 10
    
        -m <machine_friendly_mode>  default value = false
4. Run the script using ```python script.py```