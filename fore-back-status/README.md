# ForeBackStatus
ForegroundBackgroundStatus

http://www.jianshu.com/p/d243b32b699c

写在前面：
最近做项目有一个锁屏的功能，需求是：当APP从后台切到前台时，需要打开锁屏页面（5分钟内不锁屏）<br>
APP前后台说明<br>
　　前台：有一个或多个Activity可见<br>
　　后台：应用内所有Activity不可见<br>
兼容性：Android4.0及以上 [ActivityLifecycleCallbacks](https://developer.android.google.cn/reference/android/app/Application.ActivityLifecycleCallbacks.html) added in [API level 14] <br> 


>参考 http://blog.csdn.net/goodlixueyong/article/details/50543627 <br>
在Activity的onStart和onStop方法中用变量count计数，在onStart中将变量加1，onStop中减1。 <br>
假设应用有两个Activity，分别为A和B。 <br>
情况1，首先启动A，A再启动B，然后关掉B： <br>
　　启动A，count=1；  <br>
　　A启动B，先B.onStart 然后A.onStop，count先加1后减1，count为1。 <br>
　　关掉B，先A.onStart 然后B.onStop，count先加1后减1，count为1 <br>
 情况2，首先启动A，然后按Home键返回桌面： <br>
　　启动A，count=1， <br>
　　按Home键返回桌面，会执行A.onStop，count的计数变位0。 <br>
以上，可以通过对count的值来判断应用从前后台状态。 <br>


这篇文章给了我思路，可以通过Activity计数来实现前后台状态的判断，其它方法都没有该方法简洁高效。<br>
但是实践的时候发现只能过变量计数的方法，不能完美解决问题。<br>
问题：当应用第一次启动时，仍然会调用app从后台切到前台的方法。（我这里需要屏蔽掉这种情况）<br>

仔细琢磨发现可以再加一个条件进行判断，增加一个最后一次可见的Activity变量（也可以增加一个后台超时时间），在执行onResume方法时进行判断最后一次可见Activity是否是当前可见的Activity，就可以解决以上问题<br>

#写在最后：
因为项目需要，我只实现了后台切换到前台的功能，聪明如你，实现后台切换到前台也炒鸡简单吧<br>
