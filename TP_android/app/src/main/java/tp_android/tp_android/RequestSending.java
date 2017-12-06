package tp_android.tp_android;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;


public class RequestSending  {

    private Context context;
    private ObjectAnimator anim;
    private PopupWindow popupWindow;
    private View customView;


    public RequestSending(Context context, Activity activity, LinearLayout mLinearLayout) {
        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
        customView = inflater.inflate(R.layout.circle_progress_layout,null);

        ProgressBar mprogressBar = (ProgressBar) customView.findViewById(R.id.circular_progress_bar);


        popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        if(Build.VERSION.SDK_INT>=21){
            popupWindow.setElevation(5.0f);
        }
        popupWindow.showAtLocation(mLinearLayout, Gravity.CENTER,0,0);

        anim = ObjectAnimator.ofInt(mprogressBar, "progress", 0, 100);
        anim.setDuration(2000);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.start();


    }

    public void stopButton(final RequestQueue queue) {
        Button stopSendButton = (Button) customView.findViewById(R.id.stop_send);
        stopSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queue.cancelAll(new RequestQueue.RequestFilter() {
                    @Override
                    public boolean apply(Request<?> request) {
                        stop();
                        return true;
                    }
                });
            }
        });
    }


    public void stop() {
        anim.pause();
        popupWindow.dismiss();
    }

}
