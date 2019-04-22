/*
 *  Copyright (C) 2017 The OmniROM Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
*/
package org.omnirom.omnigears;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.provider.SearchIndexableResource;

import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.omni.PackageUtils;
import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settingslib.core.AbstractPreferenceController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OmniDashboardFragment extends DashboardFragment {

    private static final String TAG = "OmniDashboardFragment";
    public static final String CATEGORY_OMNI = "com.android.settings.category.ia.omni";
    private static final String KEY_DEVICE_PARTS = "device_parts";
    private static final String KEY_DISPLAY_MANAGER = "display_manager";
    private static final String KEY_BATTERY_LIGHTS = "led_settings";
    private static final String KEY_FINGERPRINT_SETTINGS = "fingerprint_settings";

    private static final String PACKAGE_DEVICE_PARTS = "org.omnirom.device";
    private static final String PACKAGE_DISPLAY_MANAGER = "org.omnirom.omnidisplaymanager";

    private FingerprintManager mFingerprintManager;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        super.onCreatePreferences(savedInstanceState, rootKey);
        if (!PackageUtils.isAvailableApp(PACKAGE_DEVICE_PARTS, getContext())) {
            Preference pref = getPreferenceScreen().findPreference(KEY_DEVICE_PARTS);
            if (pref != null) {
                getPreferenceScreen().removePreference(pref);
            }
        }
        if (!PackageUtils.isAvailableApp(PACKAGE_DISPLAY_MANAGER, getContext())) {
            Preference pref = getPreferenceScreen().findPreference(KEY_DISPLAY_MANAGER);
            if (pref != null) {
                getPreferenceScreen().removePreference(pref);
            }
        }
        if (!getResources().getBoolean(com.android.internal.R.bool.config_intrusiveBatteryLed)) {
            Preference pref = getPreferenceScreen().findPreference(KEY_BATTERY_LIGHTS);
            if (pref != null) {
                getPreferenceScreen().removePreference(pref);
            }
        }

        mFingerprintManager = (FingerprintManager) getActivity().getSystemService(Context.FINGERPRINT_SERVICE);
        if (mFingerprintManager == null || !mFingerprintManager.isHardwareDetected()){
            Preference pref = getPreferenceScreen().findPreference(KEY_FINGERPRINT_SETTINGS);
            if (pref != null) {
                getPreferenceScreen().removePreference(pref);
            }
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.OMNI_SETTINGS;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.omni_dashboard_fragment;
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {

                public List<SearchIndexableResource> getXmlResourcesToIndex(
                        Context context, boolean enabled) {
                    final SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.omni_dashboard_fragment;
                    return Arrays.asList(sir);
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    ArrayList<String> result = new ArrayList<String>();
                    if (!context.getResources().getBoolean(com.android.internal.R.bool.config_intrusiveBatteryLed)) {
                        result.add(KEY_BATTERY_LIGHTS);
                    }
                    return result;
                }
            };
}
