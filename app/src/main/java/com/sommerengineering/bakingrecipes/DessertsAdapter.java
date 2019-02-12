package com.sommerengineering.bakingrecipes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

// populates the recycler grid in MainActivity
public class DessertsAdapter extends RecyclerView.Adapter<DessertsAdapter.DessertViewHolder> {

    // click handler for the recycler items
    public interface DessertAdapterOnClickHandler {
        void onRecyclerItemClick(Dessert dessert);
    }

    // member variables
    private ArrayList<Dessert> mDesserts;
    private final Context mContext;
    private final DessertAdapterOnClickHandler mClickHandler;

    // constructor
    public DessertsAdapter(Context context, ArrayList<Dessert> desserts, DessertAdapterOnClickHandler clickHandler) {

        // initialize member variables
        mContext = context;
        mDesserts = desserts;
        mClickHandler = clickHandler;
    }

    // viewholder subclass allows for caching of views which decreases device memory usage
    class DessertViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // a single textview consumes the entire recycler item and displays the dessert name
        private final TextView mDessertNameTv;

        // constructor puts a click listener on this textview
        DessertViewHolder(View itemView) {

            super(itemView);
            mDessertNameTv = itemView.findViewById(R.id.tv_dessert_name);
            itemView.setOnClickListener(this);
        }

        // on item click pass the Dessert object to MainActivity through the handler interface
        @Override
        public void onClick(View itemView) {

            // get the clicked movie and pass to the custom handler
            int position = getAdapterPosition();
            Dessert dessert = mDesserts.get(position);
            mClickHandler.onRecyclerItemClick(dessert);
        }

        // set the text in the item textview
        void bind(String recipeName) {
            mDessertNameTv.setText(recipeName);
        }
    }

    @NonNull
    @Override
    public DessertViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        // inflate the recycler item layout
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.recycler_item, viewGroup, false);

        // pass the inflated layout to the ViewHolder constructor
        return new DessertViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DessertViewHolder viewHolder, int position) {

        // check that the list of desserts exists and has been properly initialized
        if (mDesserts != null && mDesserts.size() > 0) {

            // get current Dessert
            Dessert dessert = mDesserts.get(position);

            // call into holder class to bind data to textview for this item
            String recipeName = dessert.getName();
            viewHolder.bind(recipeName);
        }
    }

    @Override
    public int getItemCount() {
        if (mDesserts == null) return 0;
        return mDesserts.size();
    }

    // clear everything from adapter
    public void clear() {
        mDesserts.clear();
    }

    // overwrite all contents in adapter
    public void addAll(ArrayList<Dessert> desserts) {
        mDesserts = desserts;
    }
}
