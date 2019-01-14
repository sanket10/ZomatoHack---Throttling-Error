import numpy as np

data = np.loadtxt('Monday.txt')

text_file = open("MondayData.txt", "w")

for d in data:
    u = d
    sd = u*0.1
    L =np.random.normal(u,sd,10)
    res = ",".join(map(str,L))
    text_file.write(res + '\n')

text_file.close()

d