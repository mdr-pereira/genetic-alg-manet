import matplotlib.pyplot as plt

def readfile():
    # Using readlines()
    file1 = open('stats.txt', 'r')
    Lines = file1.readlines()
    # Strips the newline character
    x = []
    y = []
    for line in Lines:
       data = line.split(",")
       x.append(int(data[0]))
       y.append(float(data[1].replace("\n","")))
    return x,y

def myplot(x,y):
    # plotting
    plt.title("Fitness evolution")
    plt.xlabel("Generations")
    plt.ylabel("Fitness")
    plt.plot(x, y, color ="red", marker ='.')
    plt.text(x[0]+2,y[0],y[0])
    plt.text(x[-1],y[-1],y[-1])
    plt.savefig('fitnessLevel.png', bbox_inches='tight')
    plt.show()
    

if __name__ == "__main__":
    x,y= readfile()
    myplot(x,y)
