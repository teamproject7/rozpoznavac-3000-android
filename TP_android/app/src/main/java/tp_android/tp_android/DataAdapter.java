package tp_android.tp_android;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    private ArrayList<GridViewItem> android;
    private Context context;

    public DataAdapter(Context context,ArrayList<GridViewItem> android) {
        this.android = android;
        this.context = context;
    }

    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataAdapter.ViewHolder viewHolder, int i) {
        String path = android.get(i).getPath();
        String prefix = android.get(i).getPrefix();

            if (prefix.equals("")){
                Picasso.with(context).load(new File(android.get(i).getPath())).resize(600, 600).onlyScaleDown().into(viewHolder.img_android);
            }
            else {
                if(prefix.equals("SD/")){
                    if(path.contains("Camera")){
                        Picasso.with(context).load(R.drawable.sd_camera).resize(600, 600).onlyScaleDown().into(viewHolder.img_android);
                    }
                    if(path.contains("100ANDRO")){
                        Picasso.with(context).load(R.drawable.sd_camera).resize(600, 600).onlyScaleDown().into(viewHolder.img_android);
                    }
                    if(path.contains("Download")){
                        Picasso.with(context).load(R.drawable.sd_download).resize(600, 600).onlyScaleDown().into(viewHolder.img_android);
                    }
                    if(path.contains("spz_egov")){
                        Picasso.with(context).load(R.drawable.sd_spz_egov).resize(600, 600).onlyScaleDown().into(viewHolder.img_android);
                    }
                }
                else if(prefix.equals("PHONE/")){
                    if(path.contains("Camera")){
                        Picasso.with(context).load(R.drawable.phone_camera).resize(600, 600).onlyScaleDown().into(viewHolder.img_android);
                    }
                    if(path.contains("100ANDRO")){
                        Picasso.with(context).load(R.drawable.phone_camera).resize(600, 600).onlyScaleDown().into(viewHolder.img_android);
                    }
                    if(path.contains("Download")){
                        Picasso.with(context).load(R.drawable.phone_download).resize(600, 600).onlyScaleDown().into(viewHolder.img_android);
                    }
                    if(path.contains("spz_egov")){
                        Picasso.with(context).load(R.drawable.phone_spz_egov).resize(600, 600).onlyScaleDown().into(viewHolder.img_android);
                    }
                }
            }
    }

    @Override
    public int getItemCount() {
        return android.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,  View.OnLongClickListener {
        private ImageView img_android;
        ViewHolder(View view) {
            super(view);
            img_android = (ImageView) view.findViewById(R.id.img_android);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClickListener(v, getAdapterPosition(), "");
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mItemClickListener2 != null) {
                mItemClickListener2.onItemLongClickListener(v, getAdapterPosition(), "");
            }
            return false;
        }
    }

        private onRecyclerViewItemClickListener mItemClickListener;
        private onRecyclerViewItemLongClickListener mItemClickListener2;

        public void setOnItemClickListener(onRecyclerViewItemClickListener mItemClickListener) {
            this.mItemClickListener = mItemClickListener;
        }

        public void setOnLongItemClickListener(onRecyclerViewItemLongClickListener mItemClickListener) {
            this.mItemClickListener2 = mItemClickListener;
        }

        public interface onRecyclerViewItemClickListener {
            void onItemClickListener(View view, int position, String places_name);

        }
        public interface onRecyclerViewItemLongClickListener {
            void onItemLongClickListener(View view, int position, String places_name);
        }

}