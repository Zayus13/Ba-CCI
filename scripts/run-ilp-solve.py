#!/usr/bin/env python

# run the experiments for all relevant values of k and one specific choice of algorithm

from __future__ import print_function
from subprocess import call
import traceback
import argparse
import subprocess as sp
import multiprocessing as mp
import random
import shlex
import sys
import time

parser = argparse.ArgumentParser(description='A script for running the ILP solver on the real-world and random graphs.')


def work(in_file):
    # each line in the file contains problem number with parameters, graph file, parameter k and user provided upper bound
    split_line = shlex.split(in_file)
    # first entry is problem number
    data_file = split_line[0]
    c = split_line[1]
    k = split_line[2]
    print(data_file, c, k)
    sp.call(["java", "-jar", "ccmImpl.jar", "solve", data_file, c, k])
    return 0


if __name__ == '__main__':
    files = []
    # experiments for real-world instances

    for line in open("../data/data_ilp.txt"):
        if not line.startswith("#"):
            infos = shlex.split(line)
            k = int(infos[2])
            path = "\"" + infos[0] + "\""
            if int(infos[2]) < 100:
                for i in range(0, k + 1):
                    files.append(path + " " + infos[1] + " " + str(i))
                    # print(path + " " + infos[1] + " " + str(i))
            else:
                for i in range(0, 11):  # all values from 0 to 10
                    files.append(path + " " + str(infos[1]) + " " + str(i))
                    # print(path + " " + str(infos[1]) + " " + str(i))
                for i in range(1, 21):  # every 5 %
                    files.append(path + " " + str(infos[1]) + " " + str(int(k * i * 0.05)))
                    # print(path + " " + str(infos[1]) + " " + str(int(k * i * 0.05)))

    print(files)
    print(len(files))
    count = 12  # mp.cpu_count()
    random.shuffle(files)
    pool = mp.Pool(processes=count)

    # Run the jobs

    pool.map(work, files)
