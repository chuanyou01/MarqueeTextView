# MarqueeTextView
Android MarqueeTextView
## HOW to Use

 ```
 <com.zeng.chuan.marquee.MarqueeTextView
        android:id="@+id/mt_content"
        android:layout_width="150dp"
        android:layout_height="wrap_content"
        android:background="#eeeeee"
        android:maxWidth="80dp"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:marqueeTimes="3"  
        app:speed="2px"
        app:text="Hello World!1231231231231231231231"
        app:textAlign="center"
        app:textColor="#3300ff"
        app:textSize="20sp" /> ```
        
marqueeTimes  //times of marquee , -1 infinit
speed         //change the speed of marquee
text, textColor, textSize etc. attr same as TextView


