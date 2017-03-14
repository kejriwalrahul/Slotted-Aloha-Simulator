"""
	Program for running Slotted Aloha Simulation multiple timesand graphing results

	Written by Rahul Kejriwal
"""

import subprocess
import matplotlib.pyplot as plt
# from tqdm import tqdm 

# Simulation params
N = 50
P = [0.01, 0.02, 0.03, 0.05, 0.1]
W = [2, 4]
M = 100000

# Obtain simulation results
results = []
for w in W:
	curr_res = []

	for p in P:
		ops = [0,0,0,0,0,0]
		for i in range(5):
			trial_op = subprocess.check_output(['java','sender','-N', str(N), '-W', str(w), '-p', str(p), '-M', str(M), '-m', '-r', '1000'])
			tops = trial_op.split("\n")
			for i in range(len(ops)):
				ops[i] += float(tops[i]) 

		ops = [op/5 for op in ops]
		print ops
		curr_res.append(ops)

	results.append(curr_res)

# Plot Graph
for idx, w in enumerate(W):
	x = [t[3] for t in results[idx]]
	y = [t[4] for t in results[idx]]
	print x,y	
	plt.plot(x,y)
	plt.plot(x,y, 'ro')
plt.show()