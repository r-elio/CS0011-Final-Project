package android.application.meta;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ActivityListAdapter extends RecyclerView.Adapter<ActivityListAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private List<ActivityItem> activityItems;

    private ItemClickListener itemClickListener;
    private ItemLongClickListener itemLongClickListener;

    static int selectedPosition = -1;

    ActivityListAdapter(Context context, List<ActivityItem> activityItems){
        this.mInflater = LayoutInflater.from(context);
        this.activityItems = activityItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.activitylist_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String activity = activityItems.get(position).getName();
        holder.textView.setText(activity);
        if (selectedPosition == position){
            holder.itemView.setBackgroundResource(R.color.colorPrimary);
        }
        else {
            holder.itemView.setBackgroundResource(R.color.windowBackground);
        }
    }

    @Override
    public int getItemCount() {
        return activityItems.size();
    }

    ActivityItem getItem(int id){
        return activityItems.get(id);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        TextView textView;

        ViewHolder(View itemView){
            super(itemView);
            textView = itemView.findViewById(R.id.activity_name);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(view, getAdapterPosition());
                notifyDataSetChanged();
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (itemLongClickListener != null)
                return itemLongClickListener.onItemLongClick(view, getAdapterPosition());
            else
                return false;
        }
    }

    void setClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    void setLongClickListener(ItemLongClickListener itemLongClickListener){
        this.itemLongClickListener = itemLongClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface ItemLongClickListener {
        boolean onItemLongClick(View view, int position);
    }
}
