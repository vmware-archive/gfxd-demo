#!/usr/bin/env python
#
# This script is supposed to produce synthesized load usage data on a per
# 'appliance' basis. Unfortunately it does not provide enough variation thus
# producing very obviously synthesized results.
#
# Currently not used for anything, but perhaps someone will feel the urge to
# improve it at some point.

from __future__ import print_function

import math
import random
import datetime
import sys
import optparse
import json
import pickle


class OnOffDistribution:
    '''
    A class which is supposed to represent an on/off distribution of an
    electrical appliance over a 24-hour period. The inputs include the
    granularity of the state, i.e. how frequently switching occurs and the
    probability of the state across 24 hours.
    '''

    def __init__(self, interval_size=5, probability=0.5):
        ''' Default interval is 5 minutes with a probability of 0.5. i.e. the
            switch will be on for 12 hours a day and will be on for 5 minutes
            at a time. @ 5 minutes there will be 288 time slices per day,
            so 144 of these will be on.
        '''
        if probability > 1:
            probability = 1

        intervals = int((24 * 60) / interval_size)
        on_intervals = int(intervals * probability)
        off_intervals = intervals - on_intervals

        self.dist = []

        tmp_dist = [1 for i in range(on_intervals)] + [0 for i in range(off_intervals)]
        random.shuffle(tmp_dist)

        for i in tmp_dist:
            self.dist += [i for j in range(interval_size / 5)]

    def __getitem__(self, i):
        return self.dist[i]

    def __str__(self):
        return "[" + ", ".join(str(x) for x in self.dist) + "]"


class Appliance:
    '''
    A class which represents an electrical appliance. It is instantiated with
    an OnOffDistribution and a target load. Calling load(), with a given
    interval, will produce a load value of either 0 or a Brownian-derived value
    distributed about the target load.
    '''

    def __init__(self, on_off_dist, target_load, weight=1):
        self.on_off_dist = on_off_dist
        self.last_load = target_load * weight
        self.weight = weight
        self.mu = 0
        self.sigma = 1
        self.dt = 0.01

    def load(self, interval):
        if self.on_off_dist[interval] == 1:
            z = random.gauss(0.0, 1.0)
            p = self.last_load * math.exp((self.mu - 0.5 * self.sigma ** 2) *
                    self.dt + self.sigma * math.sqrt(self.dt) * z)
            self.last_load = p
        else:
            p = 0
        return round(p, 2)


class ApplianceFactory:
    '''
    A class which produces an appliance of a specific type with some variation
    in on/off distribution.
    '''

    # [ interval, probability, load ]
    # The first 2 values are used to create the OnOffDistribution and the 3rd
    # is used for the Appliance instance.
    inventory = {
        "fridge":       [60, 1, 100],    # On all the time @ 100W
        "tv":           [30, 0.2, 150],  # On for 1/2hr at a time, for 20% of the day @ 150W
        "central-ac":   [30, 0.18, 3000],
        "computer-1":   [60, 1, 40],
        "computer-2":   [60, 0.1, 25],
        "computer-3":   [60, 0.3, 30],
        "computer-4":   [5, 0.08, 20],
        "fan":          [30, 0.4, 160],
        "dishwasher":   [120, 0.1, 800],
        "oven":         [60, 0.2, 2625],
        "lights-1":     [60, 0.1, 60],
        "lights-2":     [30, 0.4, 60],
        "lights-3":     [60, 0.2, 200],
        "lights-4":     [60, 0.1, 100],
        "lights-5":     [60, 0.5, 40],
        "lights-6":     [60, 0.05, 100],
        "lights-7":     [60, 0.1, 500],
        "hot-tub":      [30, 0.18, 2300],
        "cfl-lights-1": [60, 0.5, 25],
        "cfl-lights-2": [60, 0.2, 25]
    }

    counter = 0

    def __init__(self, appliance_type):
        pass

    @staticmethod
    def create(name, weight=1):
        datum = ApplianceFactory.inventory[name]
        id = ApplianceFactory.counter
        ApplianceFactory.counter += 1
        return (id, Appliance(OnOffDistribution(datum[0], datum[1]), datum[2], weight))

    @staticmethod
    def appliances():
        return ApplianceFactory.inventory.keys()


class House:
    '''
    This class is simply a container for a bunch of appliances
    '''

    def __init__(self, weight=1):
        self._appliances = {}
        inventory = ApplianceFactory.appliances()
        random.shuffle(inventory)
        # Pick some number of appliances - anywhere from 1/2 to the full list
        x = int(random.random() * (len(inventory) / 2)) + len(inventory) / 2

        for i in range(x):
            (id, appliance) = ApplianceFactory.create(inventory[i], weight)
            self._appliances[id] = appliance

    def load(self, interval):
        result = {}
        for i in self._appliances.iterkeys():
            result[i] = self._appliances[i].load(interval)

        return result

    def appliances(self):
        return self._appliances


def do_generate(opts):
    '''
    Generate load data events
    '''
    total_houses = opts.houses

    if opts.output_file:
        output_file = open(opts.output_file, "w")
    else:
        output_file = sys.stdout

    houses = generate_houses(opts)

    epoch = datetime.datetime.fromtimestamp(0)
    time = datetime.datetime.now() - datetime.timedelta(minutes=10)
    one_second = datetime.timedelta(seconds=1)

    idx = 0
    while True:
        if idx % 10000 == 0:
            print(idx, file=sys.stderr)
        stamp = int((time - epoch).total_seconds())
        interval = (time.hour * 60 + time.minute) / 5
        for i in range(total_houses):
            for k, v in houses[i].load(interval).iteritems():
                print("{0},{1},{2},1,{3},0,{4},{5},{6}".format(idx, stamp, v, k, i, ((time.weekday() + 1) % 7) + 1, interval), file=output_file)
                idx += 1

        time += one_second


def do_averages(opts):
    '''
    Here we generate pre-computed load averages
    '''

    load_multiplier = opts.load_multiplier

    if opts.output_file:
        output_file = open(opts.output_file, "w")
    else:
        output_file = sys.stdout

    houses = generate_houses(opts)
    for h, house in houses.iteritems():
        for w in range(1, 8):
            for s in range(288):
                for k, v in house.load(s).iteritems():
                    # house_id, household_id, plug_id, weekday, time_slice, total_load, event_countinteger
                    print("{0},1,{1},{2},{3},{4},{5}".format(h, k, w, s, v, load_multiplier), file=output_file)


def do_profile(opts):
    houses = generate_houses(opts)

    x = OnOffDistribution()
    print(json.dumps(x.__dict__))

    print(pickle.dump(x, sys.stdout))


def generate_houses(opts):
    load_multiplier = opts.load_multiplier
    num_houses = opts.houses

    houses = {}
    for h in range(num_houses):
        houses[h] = House(load_multiplier)

    return houses

def do_debug(opts):
    epoch = datetime.datetime.fromtimestamp(0)
    seconds = int((datetime.datetime.now() - epoch).total_seconds())
    print(seconds)

################################  Mainline  ####################################

p = optparse.OptionParser()
p.add_option("-m", dest="mode", choices=["debug", "averages", "generate", "profile"], default="generate")
p.add_option("-d", dest="houses", type="int", default=1)
p.add_option("-l", dest="load_multiplier", type="int", default=1)
p.add_option("-o", dest="output_file")

opts, args = p.parse_args()

if opts.mode == "generate":
    do_generate(opts)
elif opts.mode == "averages":
    do_averages(opts)
elif opts.mode == "profile":
    do_profile(opts)
elif opts.mode == "debug":
    do_debug(opts)


