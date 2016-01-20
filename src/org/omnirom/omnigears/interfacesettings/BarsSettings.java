/*
 *  Copyright (C) 2015 The OmniROM Project
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

package org.omnirom.omnigears.interfacesettings;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.preference.PreferenceCategory;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.R;
import com.android.internal.util.omni.DeviceUtils;
import com.android.settings.Utils;
import com.android.settings.preference.SeekBarPreference;
import com.android.settings.preference.SystemCheckBoxPreference;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;

import com.android.internal.util.omni.OmniSwitchConstants;
import com.android.internal.util.omni.PackageUtils;

import java.util.List;
import java.util.ArrayList;

public class BarsSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener, Indexable {
    private static final String TAG = "BarsSettings";

    private static final String NAVIGATION_BAR_CATEGORY = "navbar_category";
    private static final String NAVIGATION_BAR_RECENTS_STYLE = "navbar_recents_style";
    private static final String NAVIGATION_BAR_TABLET_STYLE = "enable_tablet_navigation";
    private static final String NETWORK_TRAFFIC_STATE = "network_traffic_state";
    private static final String NETWORK_TRAFFIC_UNIT = "network_traffic_unit";
    private static final String NETWORK_TRAFFIC_PERIOD = "network_traffic_period";
    private static final String NETWORK_TRAFFIC_AUTOHIDE = "network_traffic_autohide";
    private static final String NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD = "network_traffic_autohide_threshold";

    private ListPreference mNavbarRecentsStyle;
    private ListPreference mNetTrafficState;
    private ListPreference mNetTrafficUnit;
    private ListPreference mNetTrafficPeriod;
    private SystemCheckBoxPreference mNetTrafficAutohide;
    private SeekBarPreference mNetTrafficAutohideThreshold;

    private int mNetTrafficVal;
    private int MASK_UP;
    private int MASK_DOWN;
    private int MASK_UNIT;
    private int MASK_PERIOD;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.bars_settings);
        loadResources();

        PreferenceScreen prefScreen = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

        final boolean showNavBar = DeviceUtils.deviceSupportNavigationBar(getActivity());
        final PreferenceCategory navbarCategory =
                (PreferenceCategory) prefScreen.findPreference(NAVIGATION_BAR_CATEGORY);

        // Navigationbar catagory will not be displayed when the device is not a tablet
        // or the device has physical keys
        if ((!DeviceUtils.deviceSupportNavigationBar(getActivity())) || !DeviceUtils.isTablet(getActivity())) {
            prefScreen.removePreference(findPreference(NAVIGATION_BAR_TABLET_STYLE));
        }

        mNavbarRecentsStyle = (ListPreference) findPreference(NAVIGATION_BAR_RECENTS_STYLE);
        int recentsStyle = Settings.System.getInt(resolver,
                Settings.System.NAVIGATION_BAR_RECENTS, 0);

        mNavbarRecentsStyle.setValue(Integer.toString(recentsStyle));
        mNavbarRecentsStyle.setSummary(mNavbarRecentsStyle.getEntry());
        mNavbarRecentsStyle.setOnPreferenceChangeListener(this);

        mNetTrafficState = (ListPreference) prefScreen.findPreference(NETWORK_TRAFFIC_STATE);
        mNetTrafficUnit = (ListPreference) prefScreen.findPreference(NETWORK_TRAFFIC_UNIT);
        mNetTrafficPeriod = (ListPreference) prefScreen.findPreference(NETWORK_TRAFFIC_PERIOD);
        mNetTrafficAutohide = (SystemCheckBoxPreference) prefScreen.findPreference(NETWORK_TRAFFIC_AUTOHIDE);
        mNetTrafficAutohideThreshold = (SeekBarPreference) prefScreen.findPreference(NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD);

        // TrafficStats will return UNSUPPORTED if the device does not support it.
        if (TrafficStats.getTotalTxBytes() != TrafficStats.UNSUPPORTED &&
                TrafficStats.getTotalRxBytes() != TrafficStats.UNSUPPORTED) {
            mNetTrafficVal = Settings.System.getInt(resolver, Settings.System.NETWORK_TRAFFIC_STATE, 0);
            int intIndex = mNetTrafficVal & (MASK_UP + MASK_DOWN);
            intIndex = mNetTrafficState.findIndexOfValue(String.valueOf(intIndex));

            mNetTrafficState.setValueIndex(intIndex >= 0 ? intIndex : 0);
            mNetTrafficState.setSummary(mNetTrafficState.getEntry());
            mNetTrafficState.setOnPreferenceChangeListener(this);

            mNetTrafficUnit.setValueIndex(getBit(mNetTrafficVal, MASK_UNIT) ? 1 : 0);
            mNetTrafficUnit.setSummary(mNetTrafficUnit.getEntry());
            mNetTrafficUnit.setOnPreferenceChangeListener(this);

            intIndex = (mNetTrafficVal & MASK_PERIOD) >>> 16;
            intIndex = mNetTrafficPeriod.findIndexOfValue(String.valueOf(intIndex));
            mNetTrafficPeriod.setValueIndex(intIndex >= 0 ? intIndex : 1);
            mNetTrafficPeriod.setSummary(mNetTrafficPeriod.getEntry());
            mNetTrafficPeriod.setOnPreferenceChangeListener(this);

            int netTrafficAutohideThreshold = Settings.System.getInt(getContentResolver(),
                    Settings.System.NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD, 10);
            mNetTrafficAutohideThreshold.setValue(netTrafficAutohideThreshold / 1);
            mNetTrafficAutohideThreshold.setOnPreferenceChangeListener(this);

            mNetTrafficUnit.setEnabled(intIndex != 0);
            mNetTrafficPeriod.setEnabled(intIndex != 0);
            mNetTrafficAutohide.setEnabled(intIndex != 0);
            mNetTrafficAutohideThreshold.setEnabled(intIndex != 0);
        } else {
            prefScreen.removePreference(findPreference(NETWORK_TRAFFIC_STATE));
            prefScreen.removePreference(findPreference(NETWORK_TRAFFIC_UNIT));
            prefScreen.removePreference(findPreference(NETWORK_TRAFFIC_PERIOD));
            prefScreen.removePreference(findPreference(NETWORK_TRAFFIC_AUTOHIDE));
            prefScreen.removePreference(findPreference(NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD));
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        // If we didn't handle it, let preferences handle it.
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mNavbarRecentsStyle) {
            int value = Integer.valueOf((String) newValue);
            if (value == 1) {
                if (!isOmniSwitchInstalled()){
                    doOmniSwitchUnavail();
                } else if (!OmniSwitchConstants.isOmniSwitchRunning(getActivity())) {
                    doOmniSwitchConfig();
                }
            }
            int index = mNavbarRecentsStyle.findIndexOfValue((String) newValue);
            mNavbarRecentsStyle.setSummary(
                    mNavbarRecentsStyle.getEntries()[index]);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.NAVIGATION_BAR_RECENTS, value);
        } else if (preference == mNetTrafficState) {
            int intState = Integer.valueOf((String)newValue);
            mNetTrafficVal = setBit(mNetTrafficVal, MASK_UP, getBit(intState, MASK_UP));
            mNetTrafficVal = setBit(mNetTrafficVal, MASK_DOWN, getBit(intState, MASK_DOWN));
            Settings.System.putInt(resolver, Settings.System.NETWORK_TRAFFIC_STATE, mNetTrafficVal);
            int index = mNetTrafficState.findIndexOfValue((String) newValue);
            mNetTrafficState.setSummary(mNetTrafficState.getEntries()[index]);
            mNetTrafficUnit.setEnabled(intState != 0);
            mNetTrafficPeriod.setEnabled(intState != 0);
            mNetTrafficAutohide.setEnabled(intState != 0);
            mNetTrafficAutohideThreshold.setEnabled(intState != 0);
        } else if (preference == mNetTrafficUnit) {
            // 1 = Display as Byte/s; default is bit/s
            mNetTrafficVal = setBit(mNetTrafficVal, MASK_UNIT, ((String)newValue).equals("1"));
            Settings.System.putInt(resolver, Settings.System.NETWORK_TRAFFIC_STATE, mNetTrafficVal);
            int index = mNetTrafficUnit.findIndexOfValue((String) newValue);
            mNetTrafficUnit.setSummary(mNetTrafficUnit.getEntries()[index]);
        } else if (preference == mNetTrafficPeriod) {
            int intState = Integer.valueOf((String)newValue);
            mNetTrafficVal = setBit(mNetTrafficVal, MASK_PERIOD, false) + (intState << 16);
            Settings.System.putInt(resolver, Settings.System.NETWORK_TRAFFIC_STATE, mNetTrafficVal);
            int index = mNetTrafficPeriod.findIndexOfValue((String) newValue);
            mNetTrafficPeriod.setSummary(mNetTrafficPeriod.getEntries()[index]);
        } else if (preference == mNetTrafficAutohideThreshold) {
            int threshold = (Integer) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD, threshold * 1);
        }

        return true;
    }

    private void doOmniSwitchConfig() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(R.string.omniswitch_title);
        alertDialogBuilder.setMessage(R.string.omniswitch_dialog_running)
            .setPositiveButton(R.string.omniswitch_settings,new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id) {
                    startActivity(OmniSwitchConstants.INTENT_LAUNCH_APP);
                }
            });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void doOmniSwitchUnavail() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(R.string.omniswitch_title);
        alertDialogBuilder.setMessage(R.string.omniswitch_dialog_unavail);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private boolean isOmniSwitchInstalled() {
        return PackageUtils.isAvailableApp(OmniSwitchConstants.APP_PACKAGE_NAME, getActivity());
    }

    private void loadResources() {
        Resources resources = getActivity().getResources();
        MASK_UP = resources.getInteger(R.integer.maskUp);
        MASK_DOWN = resources.getInteger(R.integer.maskDown);
        MASK_UNIT = resources.getInteger(R.integer.maskUnit);
        MASK_PERIOD = resources.getInteger(R.integer.maskPeriod);
    }

    // intMask should only have the desired bit(s) set
    private int setBit(int intNumber, int intMask, boolean blnState) {
        if (blnState) {
            return (intNumber | intMask);
        }
        return (intNumber & ~intMask);
    }

    private boolean getBit(int intNumber, int intMask) {
        return (intNumber & intMask) == intMask;
    }

    public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                        boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();

                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.bars_settings;
                    result.add(sir);

                    return result;
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    ArrayList<String> result = new ArrayList<String>();
                    return result;
                }
            };
}
