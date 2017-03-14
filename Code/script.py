import subprocess
import matplotlib.pyplot as plt
from tqdm import tqdm 

# Simulation params
N = 1000
P = [0.01, 0.02, 0.03, 0.05, 0.1]
W = [2, 4]
M = 100000

# Obtain simulation results
results = []
for w in tqdm(W):
	curr_res = []

	for p in tqdm(P):
		trial_op = subprocess.check_output(['java','sender','-N', str(N), '-W', str(w), '-p', str(p), '-M', str(M), '-m', '-r', '1000000'])
		curr_res.append(trial_op.split("\n"))
		# print trial_op.split("\n")

	results.append(curr_res)

# Plot Graph
for idx, w in enumerate(W):
	x = [t[3] for t in results[idx]]
	y = [t[4] for t in results[idx]]
	print x,y	
	plt.plot(x,y)
plt.show()