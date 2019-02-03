# Chan's Algorithm
A Java implementation of Chan's Algorithm with nicely animated progress and run time for education purposes.

**What's Chan's Algorithm?**

Look it up on [Wikipedia](https://en.wikipedia.org/wiki/Chan%27s_algorithm) ;), or read some papers about it.
It's an efficient algorithm to calculate the convex hull of points in the plane with runtime of **O(|Points| times log(|Hull|))**, so in most cases even better than **O(|Points| times log(|Points|))** or **O(|Points| times |Hull|)**.

The algorithm is based on Grahams Scan, which is executed on subsets of the points, and a modified version of Jarvis March, combining Jarvis March with binary search.
My sources were Wikipedia, [this](https://ipfs.io/ipfs/QmXoypizjW3WknFiJnKLwHCnL72vedxjQkDDP1mXWo6uco/wiki/Chan's_algorithm.html) and [this site](http://www.wikiwand.com/en/Chan%27s_algorithm).

**What's your main function?**

For running the algorithm only, you can take a look into the "chan" folder. You can remove the GfxRequest-calls in that case.
If you want to admire the beauty of the algorithm, run the RunGraphically class inside the "run" folder.

The sorting in Grahams Scan is not included in the histogram, because you can easily use library functions for it, and it's in O(|Points| times log(|Hull|)), too.

**Controls**

H = see the histogram
 - / which part of the code you want to measure
 - X scaling of the X axis
 - Y scaling of the Y axis, deviding by the chosen value

M = open the menue
 - </> are for single steps
 - <</>> are for major steps in the algorithm
 - <<</>>> are for seeing different point clouds, using a different seed for generation
 - normal means normally distributed
 - uniform means uniformly distributed
 - trian. means uniformly distributed in a triangle
 - cir0% means a circle, uniformly distributed over polar coordinates
 - cir90% means a circle, uniformly distributed over polar coordinates in the outer 100%-90% distance from origin
 - cir100% analogue
 - by clicking on n: 20, you can change the amount of points

L / D = light / dark mode

By changing
```java
hStar = Math.min(hStar * hStar, n);
``` in chan.ChansAlgorithm.java to e.g.
```java
hStar = Math.min(2 * hStar, n);
```
you can easily demonstrate with the histogram mode, why the super exponential search is necessary.

**Can I use your work for mine?**

Look up the license ;), no really! :).

**How can I thank you?**

The usual way: by one time being my boss, and paying very well ;), or by a little donation, which is equivalent to a little present, like a chocolate bar, a coffee if I would drink it, or a small expansion of my Lego collection ;), or some hardware improvements; or even software which I wished I could use, which however is expensive.

[![paypal](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif)](antonio-noack@gmx.de)