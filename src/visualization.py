import matplotlib.pyplot as plt

def readfile():
    # Using readlines()
    file1 = open('nodes.txt', 'r')
    Lines = file1.readlines()
    # Strips the newline character
    x = []
    y = []
    r = []
    for line in Lines:
       data = line.split(",")
       x.append(int(data[0]))
       y.append(int(data[1]))
       r.append(float(data[2]))
    return x,y,r

def readresults():
    # Using readlines()
    file1 = open('results.txt', 'r')
    Lines = file1.readlines()
    # Strips the newline character
    path = []
    first = True
    for line in Lines:
        if first:
            cost = float(line)
            first = False
        else:    
            data = line.replace("[","").replace("]","").split(",")
            for node in data:
                path.append(int(node))
    return path, cost

def myplot(x,y,r, start, goal):
    n = range(0, len(r))
   
    for i in range(len(r)):
        if i%17== 0:
            c = plt.Circle((x[i], y[i]), r[i], alpha=0.1, color='r')
            plt.gca().add_patch(c)
        if i == start or i == goal:
            c = plt.Circle((x[i], y[i]), 1, alpha=0.5, color='g')
            plt.gca().add_patch(c)
        # plt.text(x[i],y[i],n[i])
    plt.scatter(x, y, c='Blue')
    plt.savefig('topology.png', bbox_inches='tight')
    plt.show()

def onlyDots(x,y, cost, path, r):
    plt.title("Fitness:" + str(cost))
    # c = plt.Circle((x[start], y[start]), 1, alpha=.9, color='r')
    # plt.gca().add_patch(c)
    # c = plt.Circle((x[goal], y[goal]), 1, alpha=.9, color='r')
    # plt.gca().add_patch(c)

    # for i in range(len(r)):
    #     if i in path:
    #         c = plt.Circle((x[i], y[i]), r[i], alpha=0.2, color='r')
    #         plt.gca().add_patch(c)
    x_path = []
    y_path = []
    for i in range(len(path)):
        x_path.append(x[path[i]])
        y_path.append(y[path[i]])
    plt.plot(x_path, y_path, linestyle="-", marker="o")
    plt.scatter(x, y, c='Blue')
    plt.savefig('path.png', bbox_inches='tight')
    plt.show()

if __name__ == "__main__":
    x,y,r = readfile()
    path, cost = readresults()
    onlyDots(x,y, cost, path, r)
    #myplot(x,y,r, path[0], path[-1])
