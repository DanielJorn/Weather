package example.yuratoxa.weather;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;



public class RendererRecyclerViewAdapter extends RecyclerView.Adapter {

    @Override
    public int getItemViewType(final int position){
        final ItemModel item = getItem(position);
        return item.getType();
    }

    private ItemModel getItem(final int position){
        return mItems.get(position);
    }

    @NonNull
    private final ArrayList<ItemModel> mItems = new ArrayList<>();

    private ViewRenderer mRenderer;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        return mRenderer.createViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        //mRenderer.bindView(item, holder);
    }

    public void registerRenderer (@NonNull final ViewRenderer renderer){
        mRenderer = renderer;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setItems(@NonNull final ArrayList items) {
        mItems.clear();
        mItems.addAll(items);
    }

    interface ItemModel {
        int getType();
    }
}

