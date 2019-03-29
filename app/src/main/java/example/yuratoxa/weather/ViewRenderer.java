package example.yuratoxa.weather;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;


public abstract class ViewRenderer<M extends RendererRecyclerViewAdapter.ItemModel, VH extends RecyclerView.ViewHolder> {

    public abstract void bindView(@NonNull M model, @NonNull VH holder);

    @NonNull
    public abstract VH createViewHolder(@Nullable ViewGroup parent);
}
