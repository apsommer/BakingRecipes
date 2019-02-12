package com.sommerengineering.bakingrecipes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class DessertAdapter extends RecyclerView.Adapter<DessertAdapter.DessertViewHolder> {

    // member variables
    private ArrayList<Dessert> mDesserts;
    private final Context mContext;
    private final DessertAdapterOnClickHandler mClickHandler;

    // constructor
    public DessertAdapter(Context context, ArrayList<Dessert> desserts, DessertAdapterOnClickHandler clickHandler) {

        // initialize member variables
        mContext = context;
        mDesserts = desserts;
        mClickHandler = clickHandler;
    }

    // click handler for recycler items
    public interface DessertAdapterOnClickHandler {
        void onRecyclerItemClick(Dessert dessert);
    }

    // ViewHolder subclass allows for view caching which decreases usage of device memory
    class DessertViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // TextView displays the dessert name
        private final TextView mDessertNameTv;

        // constructor puts a click listener on the view
        DessertViewHolder(View itemView) {

            super(itemView);
            mDessertNameTv = itemView.findViewById(R.id.tv_dessert_name);
            itemView.setOnClickListener(this);
        }

        // click event gets the Dessert object and passes it to the recycler click handler
        @Override
        public void onClick(View itemView) {

            // get the clicked movie and pass to the custom handler
            int position = getAdapterPosition();
            Dessert dessert = mDesserts.get(position);
            mClickHandler.onRecyclerItemClick(dessert);
        }

        // called by the adapter to set text in the item TextView
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

        // check that the list of desserts has been properly initialized
        if (mDesserts != null && mDesserts.size() > 0) {

            // get current dessert
            Dessert dessert = mDesserts.get(position);

            // call into holder class to update the poster image for this item
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
