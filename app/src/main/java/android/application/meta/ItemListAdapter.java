package android.application.meta;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.ParseException;
import java.util.List;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private List<DateTimeItem> dateTimeItems;

    private ItemClickListener itemClickListener;
    private ItemLongClickListener itemLongClickListener;

    ItemListAdapter(Context context, List<DateTimeItem> dateTimeItems){
        this.mInflater = LayoutInflater.from(context);
        this.dateTimeItems = dateTimeItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.itemlist_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            String startStr = dateTimeItems.get(position).getStartTime();
            String endStr = dateTimeItems.get(position).getEndTime();
            holder.startTime.setText(startStr);
            holder.endTime.setText(endStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() { return dateTimeItems.size(); }

    DateTimeItem getItem(int id) { return dateTimeItems.get(id); }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {
        private TextView startTime;
        private TextView endTime;

        ViewHolder(View itemView){
            super(itemView);
            startTime = itemView.findViewById(R.id.startTime);
            endTime = itemView.findViewById(R.id.endTime);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null)
                itemClickListener.onItemClick(v, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            if (itemLongClickListener != null)
                return itemLongClickListener.onItemLongClick(v, getAdapterPosition());
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
