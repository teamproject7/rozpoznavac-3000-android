package tp_android.tp_android;


public class MyListenner {
    public interface Listener {
        public void onStateChange(boolean state);
    }

    private Listener mListener = null;
    public void registerListener (Listener listener) {
        mListener = listener;
    }

    private boolean myBoolean = false;
    public void doYourWork() {
        myBoolean = true;
        if (mListener != null)
            mListener.onStateChange(myBoolean);
    }
}