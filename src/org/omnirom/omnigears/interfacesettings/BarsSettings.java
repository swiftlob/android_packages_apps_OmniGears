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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceCategory;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;

import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.R;
import com.android.internal.logging.MetricsProto.MetricsEvent;
import com.android.internal.util.omni.DeviceUtils;
import com.android.settings.Utils;
import com.android.settings.dashboard.SummaryLoader;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;

import static android.provider.Settings.System.STATUS_BAR_BRIGHTNESS_CONTROL;
import static android.provider.Settings.System.DOUBLE_TAP_SLEEP_GESTURE;

import java.util.List;
import java.util.ArrayList;

import org.omnirom.omnigears.preference.SystemCheckBoxPreference;
import org.omnirom.omnigears.preference.SeekBarPreference;

public class BarsSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener, Indexable {
    private static final String TAG = "BarsSettings";

    private static final String NETWORK_TRAFFIC_ROOT = "category_network_traffic";
    private static final String CUSTOM_HEADER_IMAGE = "status_bar_custom_header";
    private static final String DAYLIGHT_HEADER_PACK = "daylight_header_pack";
    private static final String DEFAULT_HEADER_PACKAGE = "com.android.systemui";
    private static final String NAVIGATIONBAR_ROOT = "category_navigationbar";
    private static final String TABLET_NAVIGATION_BAR = "enable_tablet_navigation";
    private static final String CUSTOM_HEADER_IMAGE_SHADOW = "status_bar_custom_header_shadow";
    private static final String QUICK_SETTTINGS_PULLDOWN = "status_bar_quick_qs_pulldown";

    private ListPreference mDaylightHeaderPack;
    private CheckBoxPreference mCustomHeaderImage;
    private SeekBarPreference mHeaderShadow;
    private ListPreference mQuickPulldown;

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.OMNI_SETTINGS;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.bars_settings);

        PreferenceScreen prefScreen = getPreferenceScreen();

        // Navigationbar catagory will not be displayed when the device is not a tablet
        // or the device has physical keys
        //if ((!DeviceUtils.deviceSupportNavigationBar(getActivity())) || DeviceUtils.isPhone(getActivity())) {
        //    prefScreen.removePreference(findPreference(NAVIGATIONBAR_ROOT));
        //}

        // TrafficStats will return UNSUPPORTED if the device does not support it.
        //if (TrafficStats.getTotalTxBytes() == TrafficStats.UNSUPPORTED ||
        //        TrafficStats.getTotalRxBytes() == TrafficStats.UNSUPPORTED) {
        //    prefScreen.removePreference(findPreference(NETWORK_TRAFFIC_ROOT));
        //}

        //final boolean customHeaderImage = Settings.System.getInt(getContentResolver(),
        //        Settings.System.STATUS_BAR_CUSTOM_HEADER, 0) == 1;
        //mCustomHeaderImage = (CheckBoxPreference) findPreference(CUSTOM_HEADER_IMAGE);
        //mCustomHeaderImage.setChecked(customHeaderImage);

        //String settingHeaderPackage = Settings.System.getString(getContentResolver(),
        //        Settings.System.STATUS_BAR_DAYLIGHT_HEADER_PACK);
        //if (settingHeaderPackage == null) {
        //    settingHeaderPackage = DEFAULT_HEADER_PACKAGE;
        //}
        //mDaylightHeaderPack = (ListPreference) findPreference(DAYLIGHT_HEADER_PACK);

        //List<String> entries = new ArrayList<String>();
        //List<String> values = new ArrayList<String>();
        //getAvailableHeaderPacks(entries, values);
        //mDaylightHeaderPack.setEntries(entries.toArray(new String[entries.size()]));
        //mDaylightHeaderPack.setEntryValues(values.toArray(new String[values.size()]));

        //int valueIndex = mDaylightHeaderPack.findIndexOfValue(settingHeaderPackage);
        //if (valueIndex == -1) {
            // no longer found
        //    settingHeaderPackage = DEFAULT_HEADER_PACKAGE;
        //    Settings.System.putString(getContentResolver(),
        //            Settings.System.STATUS_BAR_DAYLIGHT_HEADER_PACK, settingHeaderPackage);
        //    valueIndex = mDaylightHeaderPack.findIndexOfValue(settingHeaderPackage);
        //}
        //mDaylightHeaderPack.setValueIndex(valueIndex >= 0 ? valueIndex : 0);
        //mDaylightHeaderPack.setSummary(mDaylightHeaderPack.getEntry());
        //mDaylightHeaderPack.setOnPreferenceChangeListener(this);

        //mHeaderShadow = (SeekBarPreference) findPreference(CUSTOM_HEADER_IMAGE_SHADOW);
        //final int headerShadow = Settings.System.getInt(getContentResolver(),
        //        Settings.System.STATUS_BAR_CUSTOM_HEADER_SHADOW, 0);
        //mHeaderShadow.setValue((int)(((double) headerShadow / 255) * 100));
        //mHeaderShadow.setOnPreferenceChangeListener(this);

        //mQuickPulldown = (ListPreference) findPreference(QUICK_SETTTINGS_PULLDOWN);
        //mQuickPulldown.setOnPreferenceChangeListener(this);
        //int quickPullDownValue = Settings.System.getInt(getContentResolver(), Settings.System.STATUS_BAR_QUICK_QS_PULLDOWN, 0);
        //mQuickPulldown.setValue(String.valueOf(quickPullDownValue));
        //updatePulldownSummary(quickPullDownValue);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        //if (preference == mCustomHeaderImage) {
        //    final boolean value = ((CheckBoxPreference)preference).isChecked();
        //    Settings.System.putInt(getContentResolver(),
        //            Settings.System.STATUS_BAR_CUSTOM_HEADER, value ? 1 : 0);
        //    return true;
        //}
        // If we didn't handle it, let preferences handle it.
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        //if (preference == mDaylightHeaderPack) {
        //    String value = (String) newValue;
        //    Settings.System.putString(getContentResolver(),
        //            Settings.System.STATUS_BAR_DAYLIGHT_HEADER_PACK, value);
        //    int valueIndex = mDaylightHeaderPack.findIndexOfValue(value);
        //    mDaylightHeaderPack.setSummary(mDaylightHeaderPack.getEntries()[valueIndex]);
        // } else if (preference == mHeaderShadow) {
        //    Integer headerShadow = (Integer) newValue;
        //    int realHeaderValue = (int) (((double) headerShadow / 100) * 255);
        //    Settings.System.putInt(getContentResolver(),
        //            Settings.System.STATUS_BAR_CUSTOM_HEADER_SHADOW, realHeaderValue);
        //} else if (preference == mQuickPulldown) {
        //    int quickPullDownValue = Integer.valueOf((String) newValue);
        //    Settings.System.putInt(getContentResolver(),
         //           Settings.System.STATUS_BAR_QUICK_QS_PULLDOWN, quickPullDownValue);
         //   updatePulldownSummary(quickPullDownValue);
        //}
        return true;
    }
/*
    private void getAvailableHeaderPacks(List<String> entries, List<String> values) {
        Intent i = new Intent();
        PackageManager packageManager = getPackageManager();
        i.setAction("org.omnirom.DaylightHeaderPack");
        for (ResolveInfo r : packageManager.queryIntentActivities(i, 0)) {
            String packageName = r.activityInfo.packageName;
            if (packageName.equals(DEFAULT_HEADER_PACKAGE)) {
                values.add(0, packageName);
            } else {
                values.add(packageName);
            }
            String label = r.activityInfo.loadLabel(getPackageManager()).toString();
            if (label == null) {
                label = r.activityInfo.packageName;
            }
            if (packageName.equals(DEFAULT_HEADER_PACKAGE)) {
                entries.add(0, label);
            } else {
                entries.add(label);
            }
        }
        i.setAction("org.omnirom.DaylightHeaderPack1");
        for (ResolveInfo r : packageManager.queryIntentActivities(i, 0)) {
            String packageName = r.activityInfo.packageName;
            values.add(packageName  + "/" + r.activityInfo.name);

            String label = r.activityInfo.loadLabel(getPackageManager()).toString();
            if (label == null) {
                label = packageName;
            }
            entries.add(label);
        }
    }

    private void updatePulldownSummary(int value) {
        Resources res = getResources();

        if (value == 0) {
            // quick pulldown deactivated
            mQuickPulldown.setSummary(res.getString(R.string.status_bar_quick_qs_pulldown_off));
        } else {
            String direction = res.getString(value == 2
                    ? R.string.status_bar_quick_qs_pulldown_summary_left
                    : R.string.status_bar_quick_qs_pulldown_summary_right);
            mQuickPulldown.setSummary(res.getString(R.string.status_bar_quick_qs_pulldown_summary, direction));
        }
    }
*/

    public static String toStringOnOff(boolean bool) {
        return toString(bool, "ON", "OFF");
    }

    public static String toString(boolean bool, String trueString, String falseString) {
        return bool ? trueString : falseString;
    }

    private static class SummaryProvider implements SummaryLoader.SummaryProvider {
        private final Context mContext;
        private final SummaryLoader mLoader;

        private SummaryProvider(Context context, SummaryLoader loader) {
            mContext = context;
            mLoader = loader;
        }

        @Override
        public void setListening(boolean listening) {
            if (listening) {
                updateSummary();
            }
        }

        private void updateSummary() {
            final String summary_text;
            boolean brightnessEnabled = Settings.System.getInt(mContext.getContentResolver(),
                    STATUS_BAR_BRIGHTNESS_CONTROL, 0) == 1;
            boolean dt2sEnabled = Settings.System.getInt(mContext.getContentResolver(),
                    DOUBLE_TAP_SLEEP_GESTURE, 0) == 1;
            summary_text = String.format("%s%s%s%s%s%s%s", mContext.getString(R.string.bars_brightness_summary)
                           , " ", toStringOnOff(brightnessEnabled), " / ", mContext.getString(R.string.bars_dt2s_summary)
                           , " ", toStringOnOff(dt2sEnabled));
            mLoader.setSummary(this, summary_text);
        }
    }

    public static final SummaryLoader.SummaryProviderFactory SUMMARY_PROVIDER_FACTORY
            = new SummaryLoader.SummaryProviderFactory() {
        @Override
        public SummaryLoader.SummaryProvider createSummaryProvider(Activity activity,
                                                                   SummaryLoader summaryLoader) {
            return new SummaryProvider(activity, summaryLoader);
        }
    };

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
                    if ((!DeviceUtils.deviceSupportNavigationBar(context)) ||
                            DeviceUtils.isPhone(context)) {
                        result.add(TABLET_NAVIGATION_BAR);
                    }
                    return result;
                }
            };
}
