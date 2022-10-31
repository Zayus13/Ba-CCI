
from __future__ import print_function
from subprocess import call

import argparse
import subprocess as sp
import multiprocessing as mp
import random
import shlex

parser = argparse.ArgumentParser(description='A script for running the ILP solver on the real-world and random graphs.')


def work(in_file):
    # each line in the file contains problem number with parameters, graph file, parameter k and user provided upper bound
    split_line = shlex.split(in_file)
    # first entry is problem number
    data_file = split_line[0]
    c = split_line[1]
    k = split_line[2]
    print(data_file)
    print(c)
    print(k)
    sp.call(["java", "-jar", "ccmImpl.jar", "create", data_file, c, k])
    return 0


if __name__ == '__main__':
    files = []
    # experiments for real-world instances
    for line in open("../data/data_ilp.txt"):
        if not line.startswith("#"):
            files.append(line.strip())
    print(files)
    print(len(files))
    count = 12 # mp.cpu_count()
    #random.shuffle(files)
    pool = mp.Pool(processes=count)

    # Run the jobs

    pool.map(work, files)
