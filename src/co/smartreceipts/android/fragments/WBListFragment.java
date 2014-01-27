package co.smartreceipts.android.fragments;

import wb.android.flex.Flex;
import android.app.Application;
import android.os.Bundle;
import co.smartreceipts.android.SmartReceiptsApplication;
import co.smartreceipts.android.date.DateManager;
import co.smartreceipts.android.persistence.PersistenceManager;
import co.smartreceipts.android.workers.WorkerManager;

import com.actionbarsherlock.app.SherlockListFragment;

public class WBListFragment extends SherlockListFragment {

	private SmartReceiptsApplication mApplication;
	private DateManager mDateManager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mApplication = getSmartReceiptsApplication();
		// By setting setRetainInstance to true, the Fragment will not receive subsequent calls
		// to onCreate and onDestroy. onAttach and onCreateView will still be called for the
		// new activity context
		setRetainInstance(true);
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		mApplication = null;
	}
	
	protected Flex getFlex() {
		return mApplication.getFlex();
	}
	
	protected String getFlexString(int id) {
		return mApplication.getFlex().getString(getSherlockActivity(), id);
	}
	
	protected DateManager getDateManager() {
		if (mDateManager == null) {
			mDateManager = new DateManager(getSherlockActivity(), mApplication.getPersistenceManager().getPreferences());
		}
		return mDateManager;
	}
	
	protected PersistenceManager getPersistenceManager() {
		return mApplication.getPersistenceManager();
	}
	
	protected WorkerManager getWorkerManager() {
		return mApplication.getWorkerManager();
	}
	
	public SmartReceiptsApplication getSmartReceiptsApplication() {
		if (mApplication == null) {
			final Application application = getSherlockActivity().getApplication();
			if (application instanceof SmartReceiptsApplication) {
				mApplication = (SmartReceiptsApplication) application;
			}
			else {
				throw new RuntimeException("The Application must be an instance a SmartReceiptsApplication");
			}
		}
		return mApplication;
	}
	
}
