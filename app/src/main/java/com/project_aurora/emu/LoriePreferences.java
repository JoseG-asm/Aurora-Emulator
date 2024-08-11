package com.project_aurora.emu;

import static android.Manifest.permission.POST_NOTIFICATIONS;
import static android.Manifest.permission.WRITE_SECURE_SETTINGS;
import static android.content.pm.PackageManager.PERMISSION_DENIED;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.os.Build.VERSION.SDK_INT;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.XmlResourceParser;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.Preference;

import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;

import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.SeekBarPreference;

import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.project_aurora.emu.utils.KeyInterceptor;
import com.project_aurora.emu.utils.SamsungDexUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;


import java.io.IOException;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.regex.PatternSyntaxException;

@SuppressWarnings("deprecation")
public class LoriePreferences extends AppCompatActivity {
    static final String ACTION_PREFERENCES_CHANGED = "com.project_aurora.emu.ACTION_PREFERENCES_CHANGED";
    static final String SHOW_IME_WITH_HARD_KEYBOARD = "show_ime_with_hard_keyboard";
    LoriePreferenceFragment loriePreferenceFragment;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @SuppressLint("UnspecifiedRegisterReceiverFlag")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_PREFERENCES_CHANGED.equals(intent.getAction())) {
                if (intent.getBooleanExtra("fromBroadcast", false)) {
                    loriePreferenceFragment.getPreferenceScreen().removeAll();
                    loriePreferenceFragment.addPreferencesFromResource(R.xml.preferences);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loriePreferenceFragment = new LoriePreferenceFragment();
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, loriePreferenceFragment).commit();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle("Preferences");
        }
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ACTION_PREFERENCES_CHANGED);
        registerReceiver(receiver, filter, SDK_INT >= Build.VERSION_CODES.TIRAMISU ? RECEIVER_NOT_EXPORTED : 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class LoriePreferenceFragment extends PreferenceFragmentCompat implements OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
        @Override
        public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
            SharedPreferences p = getPreferenceManager().getSharedPreferences();
            int modeValue = p == null ? 0 : Integer.parseInt(p.getString("touchMode", "1")) - 1;
            if (modeValue > 2) {
                SharedPreferences.Editor e = Objects.requireNonNull(p).edit();
                e.putString("touchMode", "1");
                e.apply();
            }

            addPreferencesFromResource(R.xml.preferences);
        }

        @SuppressWarnings("ConstantConditions")
        void updatePreferencesLayout() {
            SharedPreferences p = getPreferenceManager().getSharedPreferences();
            if (!SamsungDexUtils.available())
                findPreference("dexMetaKeyCapture").setVisible(false);
            SeekBarPreference scalePreference = findPreference("displayScale");
            SeekBarPreference capturedPointerSpeedFactor = findPreference("capturedPointerSpeedFactor");
            scalePreference.setMin(30);
            scalePreference.setMax(200);
            scalePreference.setSeekBarIncrement(10);
            scalePreference.setShowSeekBarValue(true);
            capturedPointerSpeedFactor.setMin(30);
            capturedPointerSpeedFactor.setMax(200);
            capturedPointerSpeedFactor.setSeekBarIncrement(1);
            capturedPointerSpeedFactor.setShowSeekBarValue(true);

            switch (p.getString("displayResolutionMode", "native")) {
                case "scaled":
                    findPreference("displayScale").setVisible(true);
                    findPreference("displayResolutionExact").setVisible(false);
                    findPreference("displayResolutionCustom").setVisible(false);
                    break;
                case "exact":
                    findPreference("displayScale").setVisible(false);
                    findPreference("displayResolutionExact").setVisible(true);
                    findPreference("displayResolutionCustom").setVisible(false);
                    break;
                case "custom":
                    findPreference("displayScale").setVisible(false);
                    findPreference("displayResolutionExact").setVisible(false);
                    findPreference("displayResolutionCustom").setVisible(true);
                    break;
                default:
                    findPreference("displayScale").setVisible(false);
                    findPreference("displayResolutionExact").setVisible(false);
                    findPreference("displayResolutionCustom").setVisible(false);
            }

            findPreference("dexMetaKeyCapture").setEnabled(!p.getBoolean("enableAccessibilityServiceAutomatically", false));
            findPreference("enableAccessibilityServiceAutomatically").setEnabled(!p.getBoolean("dexMetaKeyCapture", false));
            findPreference("keyCaptureOnlyWhenPointerIntercepted").setEnabled(p.getBoolean("dexMetaKeyCapture", false) || p.getBoolean("enableAccessibilityServiceAutomatically", false));
            findPreference("filterOutWinkey").setEnabled(p.getBoolean("enableAccessibilityServiceAutomatically", false));

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P)
                findPreference("hideCutout").setVisible(false);

            findPreference("displayResolutionMode").setSummary(p.getString("displayResolutionMode", "native"));
            findPreference("displayResolutionExact").setSummary(p.getString("displayResolutionExact", "1280x1024"));
            findPreference("displayResolutionCustom").setSummary(p.getString("displayResolutionCustom", "1280x1024"));
            findPreference("displayStretch").setEnabled("exact".contentEquals(p.getString("displayResolutionMode", "native")) || "custom".contentEquals(p.getString("displayResolutionMode", "native")));

            int modeValue = Integer.parseInt(p.getString("touchMode", "1")) - 1;
            String mode = getResources().getStringArray(R.array.touchscreenInputModesEntries)[modeValue];
            findPreference("touchMode").setSummary(mode);
            findPreference("scaleTouchpad").setVisible("1".equals(p.getString("touchMode", "1")) && !"native".equals(p.getString("displayResolutionMode", "native")));
            findPreference("showMouseHelper").setEnabled("1".equals(p.getString("touchMode", "1")));

            boolean requestNotificationPermissionVisible =
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                    && ContextCompat.checkSelfPermission(requireContext(), POST_NOTIFICATIONS) == PERMISSION_DENIED;
            findPreference("requestNotificationPermission").setVisible(requestNotificationPermissionVisible);
        }

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            SharedPreferences preferences = getPreferenceManager().getSharedPreferences();

            String showImeEnabled = Settings.Secure.getString(requireActivity().getContentResolver(), SHOW_IME_WITH_HARD_KEYBOARD);
            if (showImeEnabled == null) showImeEnabled = "0";
            SharedPreferences.Editor p = Objects.requireNonNull(preferences).edit();
            p.putBoolean("showIMEWhileExternalConnected", showImeEnabled.contentEquals("1"));
            p.apply();

            setListeners(getPreferenceScreen());
            updatePreferencesLayout();
        }

        void setListeners(PreferenceGroup g) {
            for (int i=0; i < g.getPreferenceCount(); i++) {
                g.getPreference(i).setOnPreferenceChangeListener(this);
                g.getPreference(i).setOnPreferenceClickListener(this);
                g.getPreference(i).setSingleLineTitle(false);

                if (g.getPreference(i) instanceof PreferenceGroup)
                    setListeners((PreferenceGroup) g.getPreference(i));
            }
        }

        @Override
        public boolean onPreferenceClick(@NonNull Preference preference) {
            if ("enableAccessibilityService".contentEquals(preference.getKey())) {
                Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivityForResult(intent, 0);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && "requestNotificationPermission".contentEquals(preference.getKey()))
                ActivityCompat.requestPermissions(requireActivity(), new String[]{ POST_NOTIFICATIONS }, 101);

            updatePreferencesLayout();
            return false;
        }

        @SuppressLint("ApplySharedPref")
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String key = preference.getKey();
            Log.e("Preferences", "changed preference: " + key);
            handler.postDelayed(this::updatePreferencesLayout, 100);

            if ("showIMEWhileExternalConnected".contentEquals(key)) {
                boolean enabled = newValue.toString().contentEquals("true");
                try {
                    Settings.Secure.putString(requireActivity().getContentResolver(), SHOW_IME_WITH_HARD_KEYBOARD, enabled ? "1" : "0");
                } catch (Exception e) {
                    if (e instanceof SecurityException) {
                        new AlertDialog.Builder(requireActivity())
                                .setTitle("Permission denied")
                                .setMessage("Android requires WRITE_SECURE_SETTINGS permission to change this setting.\n" +
                                            "Please, launch this command using ADB:\n" +
                                            "adb shell pm grant com.termux.x11 android.permission.WRITE_SECURE_SETTINGS")
                                .setNegativeButton("OK", null)
                                .create()
                                .show();
                    } else e.printStackTrace();
                    return false;
                }
            }

            if ("displayScale".contentEquals(key)) {
                int scale = (Integer) newValue;
                if (scale % 10 != 0) {
                    scale = Math.round( ( (float) scale ) / 10 ) * 10;
                    ((SeekBarPreference) preference).setValue(scale);
                    return false;
                }
            }

            if ("displayDensity".contentEquals(key)) {
                int v;
                try {
                    v = Integer.parseInt((String) newValue);
                } catch (NumberFormatException | PatternSyntaxException ignored) {
                    Toast.makeText(getActivity(), "This field accepts only numerics between 96 and 800", Toast.LENGTH_SHORT).show();
                    return false;
                }

                return (v > 96 && v < 800);
            }

            if ("displayResolutionCustom".contentEquals(key)) {
                String value = (String) newValue;
                try {
                    String[] resolution = value.split("x");
                    Integer.parseInt(resolution[0]);
                    Integer.parseInt(resolution[1]);
                } catch (NumberFormatException | PatternSyntaxException ignored) {
                    Toast.makeText(getActivity(), "Wrong resolution format", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

            if ("enableAccessibilityServiceAutomatically".contentEquals(key)) {
                if (!((Boolean) newValue))
                    KeyInterceptor.shutdown();
                if (requireContext().checkSelfPermission(WRITE_SECURE_SETTINGS) != PERMISSION_GRANTED) {
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Permission denied")
                            .setMessage("Android requires WRITE_SECURE_SETTINGS permission to start accessibility service automatically.\n" +
                                    "Please, launch this command using ADB:\n" +
                                    "adb shell pm grant com.termux.x11 android.permission.WRITE_SECURE_SETTINGS")
                            .setNegativeButton("OK", null)
                            .create()
                            .show();
                    return false;
                }
            }

            Intent intent = new Intent(ACTION_PREFERENCES_CHANGED);
            intent.putExtra("key", key);
            intent.setPackage("com.project_aurora.emu");
            requireContext().sendBroadcast(intent);

            handler.postAtTime(this::updatePreferencesLayout, 100);
            return true;
        }
    }

    public static class Receiver extends BroadcastReceiver {
        public Receiver() {
            super();
        }

        @Override
        public IBinder peekService(Context myContext, Intent service) {
            return super.peekService(myContext, service);
        }

        @SuppressLint("ApplySharedPref")
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getExtras() != null) {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                    if (intent.getStringExtra("list") != null) {
                        String result = "";
                        try {
                            XmlResourceParser parser = context.getResources().getXml(R.xml.preferences);
                            String namespace = "http://schemas.android.com/apk/res/android";
                            Map<String, ?> p = preferences.getAll();
                            int eventType = parser.getEventType();
                            while (eventType != XmlPullParser.END_DOCUMENT) {
                                if (eventType == XmlPullParser.START_TAG) {
                                    String tagName = parser.getName();

                                    if (tagName.contains("Preference") && !tagName.equals("PreferenceScreen") && !tagName.equals("PreferenceCategory")) {
                                        String key = parser.getAttributeValue(namespace, "key");
                                        String value = p.containsKey(key) ? Objects.requireNonNull(p.get(key)).toString() : parser.getAttributeValue(namespace, "defaultValue");
                                        if (key.equals("touchMode")) {
                                            String[] options0 = context.getResources().getStringArray(R.array.touchscreenInputModesEntries);
                                            String[] options1 = context.getResources().getStringArray(R.array.touchscreenInputModesValues);
                                            for (int i=0; i<options0.length; i++)
                                                if (value.contentEquals(options1[i]))
                                                    value = options0[i];
                                        }
                                        if (key.equals("transformCapturedPointer")) {
                                            String[] options0 = context.getResources().getStringArray(R.array.transformCapturedPointerEntries);
                                            String[] options1 = context.getResources().getStringArray(R.array.transformCapturedPointerValues);
                                            for (int i=0; i<options0.length; i++)
                                                if (value.contentEquals(options1[i]))
                                                    value = options0[i];
                                        }

                                        //noinspection StringConcatenationInLoop
                                        result += "\"" + key + "\"=\"" + value + "\"\n";
                                    }
                                }
                                eventType = parser.next();
                            }
                        } catch (XmlPullParserException | IOException e) {
                            throw new RuntimeException(e);
                        }
                        setResultCode(2);
                        setResultData(result);

                        return;
                    }

                    SharedPreferences.Editor edit = preferences.edit();
                    for (String key : intent.getExtras().keySet()) {
                        String newValue = intent.getStringExtra(key);
                        if (newValue == null)
                            continue;

                        switch (key) {
                            case "showIMEWhileExternalConnected": {
                                boolean enabled = "true".contentEquals(newValue);
                                try {
                                    Settings.Secure.putString(context.getContentResolver(), SHOW_IME_WITH_HARD_KEYBOARD, enabled ? "1" : "0");
                                } catch (Exception e) {
                                    if (e instanceof SecurityException) {
                                        setResultCode(1);
                                        setResultData("Permission denied.\n" +
                                                "Android requires WRITE_SECURE_SETTINGS permission to change `show_ime_with_hard_keyboard` setting.\n" +
                                                "Please, launch this command using ADB:\n" +
                                                "adb shell pm grant com.termux.x11 android.permission.WRITE_SECURE_SETTINGS");
                                        return;
                                    } else e.printStackTrace();
                                }
                                break;
                            }
                            case "displayScale": {
                                int scale = Integer.parseInt(newValue);
                                if (scale % 10 != 0) {
                                    scale = Math.round(((float) scale) / 10) * 10;
                                    edit.putInt("displayScale", scale);
                                } else
                                    edit.putInt("displayScale", scale);
                                break;
                            }
                            case "capturedPointerSpeedFactor": {
                                int v;
                                try {
                                    v = Integer.parseInt(newValue);
                                } catch (NumberFormatException | PatternSyntaxException ignored) {
                                    v = 100;
                                }
                                edit.putInt("capturedPointerSpeedFactor", Integer.parseInt(newValue));
                                break;
                            }
                            case "displayDensity": {
                                int v;
                                try {
                                    v = Integer.parseInt(newValue);
                                } catch (NumberFormatException | PatternSyntaxException ignored) {
                                    v = 0;
                                }

                                if (!(v > 96 && v < 800)) {
                                    setResultCode(1);
                                    setResultData("displayDensity accepts only numerics between 96 and 800.");
                                    return;
                                }

                                edit.putInt("displayDensity", v);
                                break;
                            }
                            case "displayResolutionCustom": {
                                try {
                                    String[] resolution = newValue.split("x");
                                    Integer.parseInt(resolution[0]);
                                    Integer.parseInt(resolution[1]);
                                } catch (NumberFormatException | PatternSyntaxException ignored) {
                                    setResultCode(1);
                                    setResultData("displayResolutionCustom: Wrong resolution format.");
                                    return;
                                }

                                edit.putString("displayResolutionCustom", newValue);
                                break;
                            }
                            case "enableAccessibilityServiceAutomatically": {
                                if (!"true".equals(newValue))
                                    KeyInterceptor.shutdown();
                                else if (context.checkSelfPermission(WRITE_SECURE_SETTINGS) != PERMISSION_GRANTED) {
                                    setResultCode(1);
                                    setResultData("Permission denied.\n" +
                                            "Android requires WRITE_SECURE_SETTINGS permission to change `show_ime_with_hard_keyboard` setting.\n" +
                                            "Please, launch this command using ADB:\n" +
                                            "adb shell pm grant com.termux.x11 android.permission.WRITE_SECURE_SETTINGS");
                                    return;
                                }

                                edit.putBoolean("enableAccessibilityServiceAutomatically", "true".contentEquals(newValue));
                                break;
                            }
                            case "displayResolutionMode":
                            case "displayResolutionExact":{
                                int array = 0;
                                switch (key) {
                                    case "displayResolutionMode":
                                        array = R.array.displayResolutionVariants;
                                        break;
                                    case "displayResolutionExact":
                                        array = R.array.displayResolution;
                                        break;
                                }
                                String[] options = context.getResources().getStringArray(array);
                                if (!Arrays.asList(options).contains(newValue)) {
                                    setResultCode(1);
                                    setResultData(key + ": can not be set to " + newValue);
                                    return;
                                }
                                edit.putString(key, newValue);
                                break;
                            }
                            case "touchMode": {
                                String[] options0 = context.getResources().getStringArray(R.array.touchscreenInputModesEntries);
                                String[] options1 = context.getResources().getStringArray(R.array.touchscreenInputModesValues);
                                boolean found = false;
                                for (int i=0; i<options0.length; i++) {
                                    if (newValue.contentEquals(options0[i])) {
                                        found = true;
                                        edit.putString(key, options1[i]);
                                        break;
                                    }
                                }

                                if (!found) {
                                    setResultCode(1);
                                    setResultData(key + ": can not be set to " + newValue);
                                    return;
                                }
                                break;
                            }
                            case "transformCapturedPointer": {
                                String[] options0 = context.getResources().getStringArray(R.array.transformCapturedPointerEntries);
                                String[] options1 = context.getResources().getStringArray(R.array.transformCapturedPointerValues);
                                boolean found = false;
                                for (int i=0; i<options0.length; i++) {
                                    if (newValue.contentEquals(options0[i])) {
                                        found = true;
                                        edit.putString(key, options1[i]);
                                        break;
                                    }
                                }

                                if (!found) {
                                    setResultCode(1);
                                    setResultData(key + ": can not be set to " + newValue);
                                    return;
                                }
                                break;
                            }
                            case "displayStretch":
                            case "Reseed":
                            case "PIP":
                            case "fullscreen":
                            case "forceLandscape":
                            case "hideCutout":
                            case "keepScreenOn":
                            case "scaleTouchpad":
                            case "showStylusClickOverride":
                            case "showMouseHelper":
                            case "pointerCapture":
                            case "tapToMove":
                            case "preferScancodes":
                            case "dexMetaKeyCapture":
                            case "filterOutWinkey":
                            case "clipboardSync": {
                                edit.putBoolean(key, "true".contentEquals(newValue));
                                break;
                            }
                            default: {
                                setResultCode(4);
                                setResultData(key + ": unrecognised option");
                                return;
                            }
                        }

                        Intent intent0 = new Intent(ACTION_PREFERENCES_CHANGED);
                        intent0.putExtra("key", key);
                        intent0.putExtra("fromBroadcast", true);
                        intent0.setPackage("com.project_aurora.emu");
                        context.sendBroadcast(intent0);
                    }
                    edit.commit();
                }

                setResultCode(2);
                setResultData("Done");
            } catch (Exception e) {
                setResultCode(4);
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                setResultData(sw.toString());
            }
        }
    }

    static Handler handler = new Handler();
}
