# -*- coding: utf-8 -*-
"""
Created on Sat Dec 10 09:34:45 2016

@author: fred
"""
import matplotlib

from matplotlib import pyplot as plt
from matplotlib import style
import numpy as np

style.use('ggplot')

time= np.loadtxt('RecACC_20161210093201.txt',
                 delimiter = ',',
                 skiprows=1,
                 usecols = (2,))
accx= np.loadtxt('RecACC_20161210093201.txt',
                 delimiter = ',',
                 skiprows=1,
                 usecols = (3,))
accy= np.loadtxt('RecACC_20161210093201.txt',
                 delimiter = ',',
                 skiprows=1,
                 usecols = (4,))
accz= np.loadtxt('RecACC_20161210093201.txt',
                 delimiter = ',',
                 skiprows=1,
                 usecols = (5,))


#plt.plot(time,accx)
#plt.plot(time,accy)
#plt.plot(time,accz)
#
#plt.title('Acc X Y Z')
#plt.ylabel('Acc')
#plt.xlabel('Time')
#
#plt.show()

fig = plt.figure()

ax1 = fig.add_subplot(311)
ax1.plot(time,accx)

ax2 = fig.add_subplot(312)
ax2.plot(time,accy)

ax3 = fig.add_subplot(313)
ax3.plot(time,accz)


# Add label:




plt.show()
