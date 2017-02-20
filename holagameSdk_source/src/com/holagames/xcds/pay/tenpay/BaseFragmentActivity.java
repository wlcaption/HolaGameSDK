package com.holagames.xcds.pay.tenpay;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class BaseFragmentActivity extends FragmentActivity {

	protected void changeFragment(Fragment target, int container, Bundle bundle, boolean addToStack) {
		if (bundle != null) {
			target.setArguments(bundle);
		}
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.replace(container, target);
		if (addToStack) {
			transaction.addToBackStack(null);
		}
		transaction.commit();
	}

	protected void addFragment(Fragment target, int container, Bundle bundle, boolean addToStack) {

		if (bundle != null) {
			target.setArguments(bundle);
		}

		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.add(container, target);

		if (addToStack) {
			transaction.addToBackStack(null);
		}

		transaction.commit();
	}

}
