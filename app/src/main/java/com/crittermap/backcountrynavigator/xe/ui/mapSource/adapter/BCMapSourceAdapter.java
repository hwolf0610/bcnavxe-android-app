package com.crittermap.backcountrynavigator.xe.ui.mapSource.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.data.model.BCMembershipType;
import com.crittermap.backcountrynavigator.xe.data.model.map.BCMap;
import com.crittermap.backcountrynavigator.xe.service.map.BCLocation;
import com.crittermap.backcountrynavigator.xe.share.BCUtils;
import com.crittermap.backcountrynavigator.xe.ui.mapSource.viewHolder.BCMapSourceViewHolder;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by henry on 3/25/2018.
 */

public class BCMapSourceAdapter extends RecyclerView.Adapter<BCMapSourceViewHolder> {
    private List<BCMap> mMapSources;
    private Context mContext;
    private OnMapItemClicked mListener;

    private String mCountry;
    private String mState;

    public BCMapSourceAdapter(List<BCMap> mapSources, Context context, OnMapItemClicked listener) {
        mMapSources = mapSources;
        mContext = context;
        mListener = listener;
    }

    @Override
    public BCMapSourceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.map_source_item, parent, false);
        return new BCMapSourceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BCMapSourceViewHolder holder, int position) {
        BCMap map = mMapSources.get(position);
        holder.mapSourceName.setText(map.getMapName());
        holder.mapSourceTag.setText(map.getMapTag());
        if (map.isFavoriteMap()) {
            holder.mapSourceFavorite.setImageDrawable(
                    ContextCompat.getDrawable(mContext, R.drawable.ic_favourite));
        } else {
            holder.mapSourceFavorite.setImageDrawable(
                    ContextCompat.getDrawable(mContext, R.drawable.ic_un_favourite));
        }
        BCMembershipType membershipType = BCMembershipType.getFromName(map.getMembershipType());
        int membershipIconId;
        if (membershipType == null || membershipType.equals(BCMembershipType.BRONZE)) {
            membershipIconId = R.drawable.ic_map_bronze_24dp;
        } else if (membershipType.equals(BCMembershipType.SILVER)) {
            membershipIconId = R.drawable.ic_map_silver_24dp;
        } else {
            membershipIconId = R.drawable.ic_map_gold_24dp;
        }

        if ((mCountry != null && !mCountry.isEmpty()) && (mState != null && !mState.isEmpty())) {
            Gson gsonMap = new Gson();
            BCLocation location = gsonMap.fromJson(map.getLocation(), BCLocation.class);
            if (location.ww.isEmpty()) {
                if (location.country.toString().contains(mCountry) || location.state.toString().contains(mState)) {
                    TypedValue outValue = new TypedValue();
                    mContext.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                    holder.itemMapSource.setBackgroundResource(outValue.resourceId);
                } else
                    holder.itemMapSource.setBackgroundResource(R.color.grey);
            } else {
                TypedValue outValue = new TypedValue();
                mContext.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                holder.itemMapSource.setBackgroundResource(outValue.resourceId);
            }
        }


        Glide.with(mContext)
                .load(membershipIconId)
                .apply(new RequestOptions().centerCrop().placeholder(R.drawable.ic_map_bronze_24dp))
                .into(holder.mapSourceMembership);

        Glide.with(mContext)
                .load(map.getImageUrl())
                .apply(new RequestOptions().centerCrop().placeholder(R.drawable.ic_logo))
                .into(holder.mapSourceLogo);
        renderRowIcon(holder, position);
    }

    @Override
    public int getItemCount() {
        return mMapSources.size();
    }


    private void renderRowIcon(final BCMapSourceViewHolder holder, final int position) {
        holder.mapSourceAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder, position);
            }
        });
        holder.mapSourceFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onFavouriteClicked(position);
            }
        });
        holder.itemMapSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(holder, position);
            }
        });
    }

    @SuppressLint("RestrictedApi")
    private void showPopupMenu(final BCMapSourceViewHolder holder, final int position) {
        final PopupMenu popup = new PopupMenu(mContext, holder.mapSourceAction, Gravity.START);
        popup.inflate(R.menu.menu_map_action);
        final BCMap map = mMapSources.get(position);

        switch (map.getMapStatus()) {
            case NOT_DOWNLOAD:
                popup.getMenu().findItem(R.id.item_map_action_delete).setVisible(false);
                break;
            case DOWNLOADED:
                popup.getMenu().findItem(R.id.item_map_action_delete).setVisible(true);
                break;
        }

        popup.getMenu().findItem(R.id.item_map_action_pin).setVisible(!map.isPinned());
        popup.getMenu().findItem(R.id.item_map_action_unpin).setVisible(map.isPinned());

        popup.setOnMenuItemClickListener(generateOnMenuItemClickOnPopup(position));
        MenuPopupHelper menuHelper = new MenuPopupHelper(mContext, (MenuBuilder) popup.getMenu(), holder.mapSourceAction);
        menuHelper.setForceShowIcon(true);
        menuHelper.show();
    }

    private PopupMenu.OnMenuItemClickListener generateOnMenuItemClickOnPopup(final int position) {
        return new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_map_action_unpin:
                        mListener.onUnpinClicked(position);
                        break;
                    case R.id.item_map_action_pin:
                        mListener.onPinClicked(position);
                        break;
                    case R.id.item_map_action_delete:
                        mListener.onDeleteClicked(position);
                        break;
                    case R.id.item_map_action_open:
                        mListener.onOpenClicked(position);
                    default:
                }
                return true;
            }
        };
    }

    public void sortMapData() {
        BCUtils.sortMapSource(mMapSources, mState, mCountry);
    }

    public interface OnMapItemClicked {
        void onDeleteClicked(int position);

        void onPinClicked(int position);

        void onUnpinClicked(int position);

        void onFavouriteClicked(int position);

        void onOpenClicked(int position);
    }

    public void setLocale(String country, String state) {
        this.mCountry = country;
        this.mState = state;
    }

}
