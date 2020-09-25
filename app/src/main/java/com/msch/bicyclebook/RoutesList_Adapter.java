package com.msch.bicyclebook;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;


public class RoutesList_Adapter extends RecyclerView.Adapter<RoutesList_Adapter.ViewHolder> {
    private static final String TAG = "RoutesList_Adapter";

    private ArrayList<String> mRV_names = new ArrayList<>();
    private ArrayList<Float> mRV_distances = new ArrayList<>();
    private ArrayList<String> mRV_times = new ArrayList<>();
    private ArrayList<String> mRV_routesIDs = new ArrayList<>();
    private ArrayList<int[]> mRV_colors = new ArrayList<>();
    private ImageView mRV_waypointIcon;
    private Button mRV_deleteButton;

    private Context mContext;

    public RoutesList_Adapter(ArrayList<String> mRV_names, ArrayList<Float> mRV_distances, ArrayList<String> mRV_times, ArrayList<int[]> mRV_colors, Button mRV_deleteBtn, ImageView mRV_waypointIcon, ArrayList<String> mRV_routesIDs, Context mContext) {
        this.mRV_names = mRV_names;
        this.mRV_distances = mRV_distances;
        this.mRV_times = mRV_times;
        this.mRV_deleteButton = mRV_deleteBtn;
        this.mRV_waypointIcon = mRV_waypointIcon;
        this.mContext = mContext;
        this.mRV_routesIDs = mRV_routesIDs;
        this.mRV_colors = mRV_colors;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.routeslist_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");
        holder.RV_name.setText(mRV_names.get(position));
        holder.RV_distance.setText(mRV_distances.get(position).toString() + " м");
        holder.RV_time.setText(mRV_times.get(position));
        int[] colorCodes;
        colorCodes = mRV_colors.get(position);
        holder.RV_colors.setBackgroundColor(Color.rgb(colorCodes[0],colorCodes[1],colorCodes[2]));

        holder.parent_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedRoute = mRV_routesIDs.get(position);
                Intent displayRouteOnMaps = new Intent(v.getContext(),MapsActivity.class);
                displayRouteOnMaps.putExtra("selectedRoute",selectedRoute);
                v.getContext().startActivity(displayRouteOnMaps);
                ((AppCompatActivity)mContext).finish();
            }
        });

        holder.RV_deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String routeFileName;
                mRV_names.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,mRV_names.size());

                routeFileName = mRV_routesIDs.get(position);
                File filePath = new File (Environment.getExternalStorageDirectory() + "/Android/data/" + "com.msch.bicyclebook" + "/savedRoutes/" + routeFileName);
                filePath.delete();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRV_names.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView RV_colors;
        ImageView RV_waypointIcon;
        TextView RV_name;
        TextView RV_distance;
        TextView RV_time;
        Button RV_deleteBtn;
        ConstraintLayout parent_layout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            RV_colors = itemView.findViewById(R.id.RV_image_background);
            RV_name = itemView.findViewById(R.id.RV_routeName);
            RV_distance = itemView.findViewById(R.id.RV_routeDistance);
            RV_time = itemView.findViewById(R.id.RV_routeTime);
            RV_deleteBtn = itemView.findViewById(R.id.RV_deleteBtn);
            RV_waypointIcon = itemView.findViewById(R.id.RV_image);
            parent_layout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
