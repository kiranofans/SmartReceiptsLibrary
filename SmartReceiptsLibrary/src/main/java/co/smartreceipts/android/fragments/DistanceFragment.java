package co.smartreceipts.android.fragments;

import android.database.SQLException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.List;

import co.smartreceipts.android.R;
import co.smartreceipts.android.adapters.DistanceAdapter;
import co.smartreceipts.android.model.Distance;
import co.smartreceipts.android.model.Trip;
import co.smartreceipts.android.model.utils.ModelUtils;
import co.smartreceipts.android.persistence.DatabaseHelper;

public class DistanceFragment extends WBListFragment implements DatabaseHelper.DistanceRowListener {

    public static final String TAG = DistanceFragment.class.getName();

    private Trip mTrip;
    private DistanceAdapter mAdapter;
    private View mProgressDialog;
    private TextView mNoDataAlert;

    public static DistanceFragment newInstance(final Trip trip) {
        final DistanceFragment fragment = new DistanceFragment();
        final Bundle args = new Bundle();
        args.putParcelable(Trip.PARCEL_KEY, trip);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getPersistenceManager().getDatabase().registerDistanceRowListener(this);
        mAdapter = new DistanceAdapter(getActivity(), getPersistenceManager().getPreferences());
        mTrip = getArguments().getParcelable(Trip.PARCEL_KEY);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.simple_card_list, container, false);
        mProgressDialog = view.findViewById(R.id.progress);
        mNoDataAlert = (TextView) view.findViewById(R.id.no_data);
        mNoDataAlert.setText(R.string.distance_no_data);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(mAdapter);
    }


    @Override
    public void onResume() {
        super.onResume();
        getPersistenceManager().getDatabase().getDistanceParallel(mTrip);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.distance_action_new) {
            final DistanceDialogFragment dialog = DistanceDialogFragment.newInstance(mTrip);
            dialog.show(getFragmentManager(), DistanceDialogFragment.TAG);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_distance, menu);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        final Distance distance = mAdapter.getItem(position);
        final DistanceDialogFragment dialog = DistanceDialogFragment.newInstance(mTrip, distance);
        dialog.show(getFragmentManager(), DistanceDialogFragment.TAG);
        getFragmentManager().executePendingTransactions();
    }

    @Override
    public void onDistanceRowsQuerySuccess(List<Distance> distance) {
        if (isAdded()) {
            mAdapter.notifyDataSetChanged(distance);
            mProgressDialog.setVisibility(View.GONE);
            if (distance == null || distance.size() == 0) {
                getListView().setVisibility(View.GONE);
                mNoDataAlert.setVisibility(View.VISIBLE);
            } else {
                mNoDataAlert.setVisibility(View.GONE);
                getListView().setVisibility(View.VISIBLE);
            }
            BigDecimal total = new BigDecimal(0);
            for (int i = 0; i < distance.size(); i++) {
                total = total.add(distance.get(i).getDistance());
            }
            getSupportActionBar().setSubtitle(getString(R.string.total_item, ModelUtils.getDecimalFormattedValue(total)));
        }
    }

    @Override
    public void onDistanceRowInsertSuccess(Distance distance) {
        if (isAdded()) {
            getPersistenceManager().getDatabase().getDistanceParallel(mTrip);
        }
    }

    @Override
    public void onDistanceRowInsertFailure(SQLException error) {
        // TODO: Add a toast message
    }

    @Override
    public void onDistanceRowUpdateSuccess(Distance distance) {
        if (isAdded()) {
            getPersistenceManager().getDatabase().getDistanceParallel(mTrip);
        }
    }

    @Override
    public void onDistanceRowUpdateFailure() {
        // TODO: Add a toast message
    }

    @Override
    public void onDistanceDeleteSuccess(Distance distance) {
        if (isAdded()) {
            getPersistenceManager().getDatabase().getDistanceParallel(mTrip);
        }
    }

    @Override
    public void onDistanceDeleteFailure() {
        // TODO: Add a toast message
    }
}