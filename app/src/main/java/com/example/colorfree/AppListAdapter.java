package com.example.colorfree;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {

    private List<ApplicationInfo> apps;
    private PackageManager pm;
    private SharedPreferences prefs;
    private Set<String> whitelist;
    private Context context;
    private AdManager adManager;

    public AppListAdapter(Context context, List<ApplicationInfo> apps) {
        this.context = context;
        this.apps = apps;
        this.pm = context.getPackageManager();
        this.prefs = context.getSharedPreferences("ColorFreePrefs", Context.MODE_PRIVATE);
        this.whitelist = prefs.getStringSet("whitelist", new HashSet<>());
        
        Collections.sort(this.apps, (a, b) -> 
            a.loadLabel(pm).toString().compareToIgnoreCase(b.loadLabel(pm).toString()));
    }

    public void setAdManager(AdManager adManager) {
        this.adManager = adManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ApplicationInfo app = apps.get(position);
        holder.appName.setText(app.loadLabel(pm));
        holder.appIcon.setImageDrawable(app.loadIcon(pm));
        
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.itemView.setOnClickListener(null);
        
        boolean isChecked = whitelist.contains(app.packageName);
        holder.checkBox.setChecked(isChecked);

        View.OnClickListener clickListener = v -> {
            holder.checkBox.setChecked(isChecked); 
            
            ChallengeRouter.showRandomChallenge(context, new ChallengeRouter.ChallengeCallback() {
                @Override
                public void onSuccess() {
                    Runnable action = () -> {
                        toggleApp(app.packageName, !isChecked);
                        holder.checkBox.setChecked(!isChecked);
                    };

                    if (adManager != null && context instanceof Activity) {
                        adManager.showAd((Activity) context, action);
                    } else {
                        action.run();
                    }
                }

                @Override
                public void onFailure() {
                    holder.checkBox.setChecked(isChecked);
                }
            });
        };

        holder.checkBox.setOnClickListener(clickListener);
        holder.itemView.setOnClickListener(clickListener);
    }

    private void toggleApp(String packageName, boolean add) {
        Set<String> newWhitelist = new HashSet<>(whitelist);
        if (add) {
            newWhitelist.add(packageName);
        } else {
            newWhitelist.remove(packageName);
        }
        whitelist = newWhitelist;
        prefs.edit().putStringSet("whitelist", whitelist).apply();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.app_icon);
            appName = itemView.findViewById(R.id.app_name);
            checkBox = itemView.findViewById(R.id.app_checkbox);
        }
    }
}
