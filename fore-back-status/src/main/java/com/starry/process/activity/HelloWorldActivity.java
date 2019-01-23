package com.starry.process.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.starry.process.R;

/**
 * @author Starry
 * @since 2017/6/3.
 */
public class HelloWorldActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_world);
        setTitle("HelloWorld");

        TextView tv_hello_world = (TextView) findViewById(R.id.tv_hello_world);
        tv_hello_world.setText("我打江南走过\n" +
                "那等在季节里的容颜如莲花的开落\n" +
                "东风不来，三月的柳絮不飞\n" +
                "你底心如小小的寂寞的城\n" +
                "恰若青石的街道向晚\n" +
                "跫音不响，三月的春帷不揭\n" +
                "你底心是小小的窗扉紧掩\n" +
                "我达达的马蹄是美丽的错误\n" +
                "我不是归人，是个过客\n" +
                "I passed through the South of Yangzi\n" +
                "The face waiting at the turn of seasons, like a lotus flower, blooms and wilts\n" +
                "Without the east wind, the willow catkins in March do not flutter\n" +
                "Your heart is like the lonesome little town\n" +
                "Like its streets of cobblestones near nightfall\n" +
                "When footfalls are silent and the bed curtains of March not unveiled\n" +
                "Your heart is a little window tightly shut\n" +
                "My clattering hooves are beautiful mistakes\n" +
                "I am not a homecoming man but a passing traveler "
        );
    }
}
