/*
 * Copyright (c) 2016. Saiy Ltd. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ai.saiy.android.api.helper;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;

import ai.saiy.android.R;
import ai.saiy.android.api.Defaults;
import ai.saiy.android.api.RequestParcel;
import ai.saiy.android.configuration.NuanceConfiguration;
import ai.saiy.android.service.SelfAware;
import ai.saiy.android.utils.MyLog;
import ai.saiy.android.utils.UtilsString;

import static ai.saiy.android.api.Defaults.ACTION.SPEAK_LISTEN;
import static ai.saiy.android.api.Defaults.ACTION.SPEAK_ONLY;
import static ai.saiy.android.api.Defaults.LanguageModel.API_AI;
import static ai.saiy.android.api.Defaults.LanguageModel.LOCAL;
import static ai.saiy.android.api.Defaults.VR.GOOGLE_CHROMIUM;
import static ai.saiy.android.api.Defaults.VR.GOOGLE_CLOUD;
import static ai.saiy.android.api.Defaults.VR.IBM;
import static ai.saiy.android.api.Defaults.VR.MICROSOFT;
import static ai.saiy.android.api.Defaults.VR.NATIVE;
import static ai.saiy.android.api.Defaults.VR.NUANCE;
import static ai.saiy.android.api.Defaults.VR.REMOTE;
import static ai.saiy.android.api.Defaults.VR.WIT;

/**
 * Helper class to OTT check the remote request parameters and make sure they contain nothing
 * erroneous, null or anything in between, despite the library already attempting to do this....
 * Once checked, we can allow {@link SelfAware} to not have to apply such
 * checks and cause clutter.
 * <p/>
 * Whilst this class can confirm the above, it cannot however be sure that the supplied parameters
 * are correctly applied and will not be declined by the API they are to be used for. Such situations
 * will be dealt with elsewhere
 * <p/>
 * Created by benrandall76@gmail.com on 25/02/2016.
 */
public final class Validation {

    private static final boolean DEBUG = MyLog.DEBUG;
    private static final String CLS_NAME = Validation.class.getSimpleName();

    private static final String _YOUR_ = "_your_";
    public static final String ID_UNKNOWN = "id_unknown";

    /**
     * Prevent instantiation
     */
    public Validation() {
        throw new IllegalArgumentException(Resources.getSystem().getString(android.R.string.no));
    }

    /**
     * Validate the {@link RequestParcel} parameters received by the remote request. At minimum,
     * they must contain a valid speech string and a request id.
     * <p/>
     * The request id may have been auto-generated by the library, rather than the caller applying it.
     * <p/>
     * If the caller intended no speech to be uttered, they needed to
     * set {@link ai.saiy.android.api.request.SaiyRequestParams#SILENCE}
     * <p/>
     * If tests fail, there will be output in the log, that a developer can spot and we decline
     * the request silently to the user.
     *
     * @param parcel the {@link RequestParcel} received from the remote request
     * @return true if the values are configured correctly enough to make an API call with them.
     */
    public static boolean validateParams(@NonNull final Context ctx, @NonNull final RequestParcel parcel) {

        final String words = parcel.getUtterance();
        final String utteranceId = parcel.getRequestId();

        if (UtilsString.notNaked(words)) {
            if (DEBUG) {
                MyLog.d(CLS_NAME, "validateParams: words: " + words);
            }

            if (UtilsString.notNaked(utteranceId)) {
                if (DEBUG) {
                    MyLog.d(CLS_NAME, "validateParams: utteranceId: " + utteranceId);
                }
                return true;
            } else {
                MyLog.e("Remote Saiy Request", ctx.getApplicationContext().getString(ai.saiy.android.R.string.error_requestid_invalid));
            }
        } else {
            MyLog.e("Remote Saiy Request", ctx.getApplicationContext().getString(R.string.error_utterance_invalid));
        }

        return false;
    }

    /**
     * Validate the {@link RequestParcel} received by the remote request.
     * <p/>
     * If tests fail, there will be output in the log, that a developer can spot and we decline
     * the request silently to the user.
     *
     * @param parcel the {@link RequestParcel} received from the remote request
     * @return true if the values are configured correctly enough to make an API call with them.
     */
    public static boolean validateParcel(@NonNull final Context ctx, final RequestParcel parcel) {
        return parcel != null && checkAction(ctx, parcel);
    }

    /**
     * Check the {@link Defaults.ACTION} is correctly defined.
     *
     * @param ctx    the application context
     * @param parcel the {@link RequestParcel}
     * @return true if the parameters are configured correctly.
     */
    private static boolean checkAction(@NonNull final Context ctx, final RequestParcel parcel) {
        if (DEBUG) {
            MyLog.i(CLS_NAME, "checkAction");
        }

        final Defaults.ACTION action = parcel.getAction();

        switch (action) {

            case SPEAK_ONLY:
                if (DEBUG) {
                    MyLog.i(CLS_NAME, "checkAction: " + SPEAK_ONLY.name());
                }
                break;
            case SPEAK_LISTEN:
                if (DEBUG) {
                    MyLog.i(CLS_NAME, "checkAction: " + SPEAK_LISTEN.name());
                }
                break;
            default:
                MyLog.e("Remote Saiy Request", ctx.getApplicationContext().getString(ai.saiy.android.R.string.error_action_invalid));
                return false;
        }

        return checkProviderTTS(ctx, parcel);
    }

    /**
     * Check the {@link Defaults.TTS} are correctly defined.
     *
     * @param ctx    the application context
     * @param parcel the {@link RequestParcel}
     * @return true if the parameters are configured correctly.
     */
    private static boolean checkProviderTTS(@NonNull final Context ctx, final RequestParcel parcel) {
        if (DEBUG) {
            MyLog.i(CLS_NAME, "checkProviderTTS");
        }

        final Defaults.TTS providerTTS = parcel.getProviderTTS();

        switch (providerTTS) {

            case LOCAL:
                if (DEBUG) {
                    MyLog.i(CLS_NAME, "checkProviderTTS: Defaults.TTS.LOCAL");
                }
                break;
            case NETWORK_NUANCE:
                if (DEBUG) {
                    MyLog.i(CLS_NAME, "checkProviderTTS: Defaults.TTS.NETWORK_NUANCE");
                }
                break;
            default:
                MyLog.e("Remote Saiy Request", ctx.getApplicationContext().getString(ai.saiy.android.R.string.error_tts_invalid));
                return false;
        }

        return checkVRProvider(ctx, parcel);
    }

    /**
     * Check the {@link Defaults.VR} are correctly defined.
     *
     * @param ctx    the application context
     * @param parcel the {@link RequestParcel}
     * @return true if the parameters are configured correctly.
     */
    private static boolean checkVRProvider(@NonNull final Context ctx, final RequestParcel parcel) {
        if (DEBUG) {
            MyLog.i(CLS_NAME, "checkVRProvider");
        }

        final Defaults.VR providerVR = parcel.getProviderVR();

        switch (providerVR) {

            case NATIVE:
                if (DEBUG) {
                    MyLog.i(CLS_NAME, "checkVRProvider: " + NATIVE.name());
                }
                break;
            case GOOGLE_CLOUD:
                if (DEBUG) {
                    MyLog.i(CLS_NAME, "checkVRProvider: " + GOOGLE_CLOUD.name());
                }
                break;
            case GOOGLE_CHROMIUM:
                if (DEBUG) {
                    MyLog.i(CLS_NAME, "checkVRProvider: " + GOOGLE_CHROMIUM.name());
                }
                break;
            case NUANCE:
                if (DEBUG) {
                    MyLog.i(CLS_NAME, "checkVRProvider: " + NUANCE.name());
                }
                break;
            case MICROSOFT:
                if (DEBUG) {
                    MyLog.i(CLS_NAME, "checkVRProvider: " + MICROSOFT.name());
                }
                break;
            case WIT:
                if (DEBUG) {
                    MyLog.i(CLS_NAME, "checkVRProvider: " + WIT.name());
                }
                break;
            case IBM:
                if (DEBUG) {
                    MyLog.i(CLS_NAME, "checkVRProvider: " + IBM.name());
                }
                break;
            case REMOTE:
                if (DEBUG) {
                    MyLog.i(CLS_NAME, "checkVRProvider: " + REMOTE.name());
                }
                break;
            default:
                switch (parcel.getLanguageModel()) {

                    case LOCAL:
                        if (DEBUG) {
                            MyLog.i(CLS_NAME, "checkVRProvider: " + LOCAL.name());
                        }
                        break;
                    case NUANCE:
                        if (DEBUG) {
                            MyLog.i(CLS_NAME, "checkVRProvider: " + NUANCE.name());
                        }
                        break;
                    case MICROSOFT:
                        if (DEBUG) {
                            MyLog.i(CLS_NAME, "checkVRProvider: " + MICROSOFT.name());
                        }
                        break;
                    case API_AI:
                        if (DEBUG) {
                            MyLog.i(CLS_NAME, "checkVRProvider: " + API_AI.name());
                        }
                        break;
                    case REMOTE:
                        if (DEBUG) {
                            MyLog.i(CLS_NAME, "checkVRProvider: " + REMOTE.name());
                        }
                        break;
                    default:
                        MyLog.e("Remote Saiy Request", ctx.getApplicationContext().getString(R.string.error_vr_invalid));
                        return false;
                }
        }

        return checkAPIKey(ctx, parcel);
    }

    /**
     * Check the API keys that will need to be used are correctly set.
     *
     * @param ctx    the application context
     * @param parcel the {@link RequestParcel}
     * @return true if the parameters are configured correctly.
     */
    private static boolean checkAPIKey(@NonNull final Context ctx, final RequestParcel parcel) {
        if (DEBUG) {
            MyLog.i(CLS_NAME, "checkAPIKey");
        }

        final Defaults.TTS providerTTS = parcel.getProviderTTS();

        switch (providerTTS) {

            case LOCAL:
                if (DEBUG) {
                    MyLog.i(CLS_NAME, "checkAPIKey: LOCAL: skipping");
                }
                break;
            case NETWORK_NUANCE:
                if (DEBUG) {
                    MyLog.i(CLS_NAME, "checkAPIKey: Defaults.TTS.NETWORK_NUANCE: skipping");
                }

                if (checkNuanceConfig(parcel)) {
                    if (DEBUG) {
                        MyLog.i(CLS_NAME, "checkAPIKey: Defaults.TTS.NETWORK_NUANCE: API key present");
                    }
                    break;
                } else {
                    MyLog.e("Remote Saiy Request", ctx.getApplicationContext().getString(R.string.error_nuance_config));
                    return false;
                }
        }

        final Defaults.VR providerVR = parcel.getProviderVR();

        switch (providerVR) {

            case NATIVE:
                if (DEBUG) {
                    MyLog.i(CLS_NAME, "checkAPIKey: " + NATIVE.name());
                }
                break;
            case GOOGLE_CLOUD:
                if (DEBUG) {
                    MyLog.i(CLS_NAME, "checkAPIKey: " + GOOGLE_CLOUD.name());
                }

                if (checkGoogleCloudConfig(parcel)) {
                    if (DEBUG) {
                        MyLog.i(CLS_NAME, "checkAPIKey: API key present");
                    }
                    break;
                } else {
                    MyLog.e("Remote Saiy Request", ctx.getApplicationContext().getString(R.string.error_api_google_cloud));
                    return false;
                }
            case GOOGLE_CHROMIUM:
                if (DEBUG) {
                    MyLog.i(CLS_NAME, "checkAPIKey: " + GOOGLE_CHROMIUM.name());
                }

                if (checkGoogleChromiumConfig(parcel)) {
                    if (DEBUG) {
                        MyLog.i(CLS_NAME, "checkAPIKey: API key present");
                    }
                    break;
                } else {
                    MyLog.e("Remote Saiy Request", ctx.getApplicationContext().getString(R.string.error_api_google_chromium));
                    return false;
                }
            case NUANCE:
                if (DEBUG) {
                    MyLog.i(CLS_NAME, "checkAPIKey: " + NUANCE.name());
                }

                if (checkNuanceConfig(parcel)) {
                    if (DEBUG) {
                        MyLog.i(CLS_NAME, "checkAPIKey: API key present");
                    }
                    break;
                } else {
                    MyLog.e("Remote Saiy Request", ctx.getApplicationContext().getString(R.string.error_nuance_config));
                    return false;
                }
            case MICROSOFT:
                if (DEBUG) {
                    MyLog.i(CLS_NAME, "checkAPIKey: " + MICROSOFT.name());
                }

                if (checkMicrosoftConfig(parcel)) {
                    if (DEBUG) {
                        MyLog.i(CLS_NAME, "checkAPIKey: API key present");
                    }
                    break;
                } else {
                    MyLog.e("Remote Saiy Request", ctx.getApplicationContext().getString(R.string.error_microsoft_config));
                    return false;
                }
            case WIT:
                if (DEBUG) {
                    MyLog.i(CLS_NAME, "checkAPIKey: " + WIT.name());
                }

                if (checkWitConfig(parcel)) {
                    if (DEBUG) {
                        MyLog.i(CLS_NAME, "checkAPIKey: API key present");
                    }
                    break;
                } else {
                    MyLog.e("Remote Saiy Request", ctx.getApplicationContext().getString(R.string.error_api_wit));
                    return false;
                }
            case IBM:
                if (DEBUG) {
                    MyLog.i(CLS_NAME, "checkAPIKey: " + IBM.name());
                }

                if (checkIBMConfig(parcel)) {
                    if (DEBUG) {
                        MyLog.i(CLS_NAME, "checkAPIKey: API key present");
                    }
                    break;
                } else {
                    MyLog.e("Remote Saiy Request", ctx.getApplicationContext().getString(R.string.error_api_ibm));
                    return false;
                }
            case REMOTE:
                if (DEBUG) {
                    MyLog.i(CLS_NAME, "checkAPIKey: " + REMOTE.name());
                }

                if (checkRemoteConfig(parcel)) {
                    if (DEBUG) {
                        MyLog.i(CLS_NAME, "checkAPIKey: API key present");
                    }
                    break;
                } else {
                    MyLog.e("Remote Saiy Request", ctx.getApplicationContext().getString(R.string.error_remote_config));
                    return false;
                }
            default:
                MyLog.e("Remote Saiy Request", ctx.getApplicationContext().getString(R.string.error_vr_invalid));
                return false;
        }

        switch (parcel.getLanguageModel()) {

            case LOCAL:
                if (DEBUG) {
                    MyLog.i(CLS_NAME, "checkAPIKey: " + LOCAL.name());
                }
                break;
            case NUANCE:
                if (DEBUG) {
                    MyLog.i(CLS_NAME, "checkAPIKey: " + NUANCE.name());
                }

                if (checkNuanceConfig(parcel)) {
                    if (DEBUG) {
                        MyLog.i(CLS_NAME, "checkAPIKey: API key present " + NUANCE.name());
                    }
                    break;
                } else {
                    MyLog.e("Remote Saiy Request", ctx.getApplicationContext().getString(ai.saiy.android.R.string.error_nuance_config));
                    return false;
                }

            case MICROSOFT:
                if (DEBUG) {
                    MyLog.i(CLS_NAME, "checkAPIKey: " + MICROSOFT.name());
                }

                if (checkMicrosoftConfig(parcel)) {
                    if (DEBUG) {
                        MyLog.i(CLS_NAME, "checkAPIKey: API key present " + MICROSOFT.name());
                    }
                    break;
                } else {
                    MyLog.e("Remote Saiy Request", ctx.getApplicationContext().getString(ai.saiy.android.R.string.error_microsoft_config));
                    return false;
                }
            case API_AI:
                if (DEBUG) {
                    MyLog.i(CLS_NAME, "checkAPIKey: " + API_AI.name());
                }

                if (checkAPIAIConfig(parcel)) {
                    if (DEBUG) {
                        MyLog.i(CLS_NAME, "checkAPIKey: API key present " + API_AI.name());
                    }
                    break;
                } else {
                    MyLog.e("Remote Saiy Request", ctx.getApplicationContext().getString(ai.saiy.android.R.string.error_api_ai_config));
                    return false;
                }
            case WIT:
                if (DEBUG) {
                    MyLog.i(CLS_NAME, "checkAPIKey: " + Defaults.LanguageModel.WIT.name());
                }

                if (checkWitConfig(parcel)) {
                    if (DEBUG) {
                        MyLog.i(CLS_NAME, "checkAPIKey: API key present");
                    }
                    break;
                } else {
                    MyLog.e("Remote Saiy Request", ctx.getApplicationContext().getString(R.string.error_api_wit));
                    return false;
                }
            case REMOTE:
                if (DEBUG) {
                    MyLog.i(CLS_NAME, "checkAPIKey: " + REMOTE.name());
                }

                if (checkRemoteConfig(parcel)) {
                    if (DEBUG) {
                        MyLog.i(CLS_NAME, "checkAPIKey: API key present " + REMOTE.name());
                    }
                    break;
                } else {
                    MyLog.e("Remote Saiy Request", ctx.getApplicationContext().getString(ai.saiy.android.R.string.error_remote_config));
                    return false;
                }
            default:
                MyLog.e("Remote Saiy Request", ctx.getApplicationContext().getString(R.string.error_vr_invalid));
                return false;

        }

        return true;
    }

    /**
     * Check the remote credentials are correctly defined
     *
     * @param parcel the {@link RequestParcel}
     * @return true if the parameters are configured correctly.
     */
    private static boolean checkRemoteConfig(@NonNull final RequestParcel parcel) {
        if (DEBUG) {
            MyLog.i(CLS_NAME, "checkRemoteConfig");
        }

        final String REMOTE_ACCESS_TOKEN = parcel.getREMOTE_ACCESS_TOKEN();
        final Uri REMOTE_SERVER_URI = parcel.getREMOTE_SERVER_URI();

        return UtilsString.notNaked(REMOTE_ACCESS_TOKEN)
                && !REMOTE_ACCESS_TOKEN.startsWith(_YOUR_)
                && REMOTE_SERVER_URI != null
                && UtilsString.notNaked(REMOTE_SERVER_URI.toString())
                && !REMOTE_SERVER_URI.toString().startsWith(_YOUR_);
    }

    /**
     * Check the API AI credentials are correctly defined
     *
     * @param parcel the {@link RequestParcel}
     * @return true if the parameters are configured correctly.
     */
    private static boolean checkAPIAIConfig(@NonNull final RequestParcel parcel) {
        if (DEBUG) {
            MyLog.i(CLS_NAME, "checkAPIAIConfig");
        }

        final String API_AI_CLIENT_ACCESS_TOKEN = parcel.getAPI_AI_CLIENT_ACCESS_TOKEN();

        return UtilsString.notNaked(API_AI_CLIENT_ACCESS_TOKEN)
                && !API_AI_CLIENT_ACCESS_TOKEN.startsWith(_YOUR_);
    }

    /**
     * Check the IBM credentials are correctly defined
     *
     * @param parcel the {@link RequestParcel}
     * @return true if the parameters are configured correctly.
     */
    private static boolean checkIBMConfig(@NonNull final RequestParcel parcel) {
        if (DEBUG) {
            MyLog.i(CLS_NAME, "checkIBMConfig");
        }

        final String IBM_SERVICE_USER_NAME = parcel.getIBM_SERVICE_USER_NAME();
        final String IBM_SERVICE_PASSWORD = parcel.getIBM_SERVICE_PASSWORD();

        return UtilsString.notNaked(IBM_SERVICE_USER_NAME)
                && !IBM_SERVICE_USER_NAME.startsWith(_YOUR_)
                && UtilsString.notNaked(IBM_SERVICE_PASSWORD)
                && !IBM_SERVICE_PASSWORD.startsWith(_YOUR_);
    }

    /**
     * Check the Wit credentials are correctly defined
     *
     * @param parcel the {@link RequestParcel}
     * @return true if the parameters are configured correctly.
     */
    private static boolean checkWitConfig(@NonNull final RequestParcel parcel) {
        if (DEBUG) {
            MyLog.i(CLS_NAME, "checkWitConfig");
        }

        final String WIT_SERVER_ACCESS_TOKEN = parcel.getWIT_SERVER_ACCESS_TOKEN();

        return UtilsString.notNaked(WIT_SERVER_ACCESS_TOKEN)
                && !WIT_SERVER_ACCESS_TOKEN.startsWith(_YOUR_);
    }

    /**
     * Check the Microsoft credentials are correctly defined
     *
     * @param parcel the {@link RequestParcel}
     * @return true if the parameters are configured correctly.
     */
    private static boolean checkMicrosoftConfig(@NonNull final RequestParcel parcel) {
        if (DEBUG) {
            MyLog.i(CLS_NAME, "checkMicrosoftConfig");
        }

        final String OXFORD_KEY_1 = parcel.getOXFORD_KEY_1();
        final String OXFORD_KEY_2 = parcel.getOXFORD_KEY_2();

        if (UtilsString.notNaked(OXFORD_KEY_1) && !OXFORD_KEY_1.startsWith(_YOUR_)
                && UtilsString.notNaked(OXFORD_KEY_2) && !OXFORD_KEY_2.startsWith(_YOUR_)) {

            switch (parcel.getLanguageModel()) {

                case MICROSOFT:
                    if (DEBUG) {
                        MyLog.i(CLS_NAME, "checkMicrosoftConfig");
                    }

                    final String LUIS_APP_ID = parcel.getLUIS_APP_ID();
                    final String LUIS_SUBSCRIPTION_ID = parcel.getLUIS_SUBSCRIPTION_ID();

                    if (UtilsString.notNaked(LUIS_APP_ID) && !LUIS_APP_ID.startsWith(_YOUR_)
                            && UtilsString.notNaked(LUIS_SUBSCRIPTION_ID) && !LUIS_SUBSCRIPTION_ID.startsWith(_YOUR_)) {
                        if (DEBUG) {
                            MyLog.i(CLS_NAME, "checkMicrosoftConfig: Credentials present");
                        }
                        return true;
                    } else {
                        if (DEBUG) {
                            MyLog.e(CLS_NAME, "checkMicrosoftConfig: NLU parameters missing");
                        }
                        return false;
                    }
                default:
                    return true;
            }
        } else {
            if (DEBUG) {
                MyLog.e(CLS_NAME, "checkMicrosoftConfig: API keys missing");
            }
            return false;
        }
    }

    /**
     * Check the Google API key is correctly defined
     *
     * @param parcel the {@link RequestParcel}
     * @return true if the parameters are configured correctly.
     */
    private static boolean checkGoogleCloudConfig(@NonNull final RequestParcel parcel) {
        if (DEBUG) {
            MyLog.i(CLS_NAME, "checkGoogleCloudConfig");
        }

        final String GOOGLE_CLOUD_ACCESS_TOKEN = parcel.getGOOGLE_CLOUD_ACCESS_TOKEN();

        return UtilsString.notNaked(GOOGLE_CLOUD_ACCESS_TOKEN) && !GOOGLE_CLOUD_ACCESS_TOKEN.startsWith(_YOUR_)
                && parcel.getGOOGLE_CLOUD_ACCESS_EXPIRY() > 0;
    }

    /**
     * Check the Google API key is correctly defined
     *
     * @param parcel the {@link RequestParcel}
     * @return true if the parameters are configured correctly.
     */
    private static boolean checkGoogleChromiumConfig(@NonNull final RequestParcel parcel) {
        if (DEBUG) {
            MyLog.i(CLS_NAME, "checkGoogleChromiumConfig");
        }

        final String GOOGLE_CHROMIUM_API_KEY = parcel.getGOOGLE_CHROMIUM_API_KEY();

        return UtilsString.notNaked(GOOGLE_CHROMIUM_API_KEY) && !GOOGLE_CHROMIUM_API_KEY.startsWith(_YOUR_);
    }

    /**
     * Check the required Nuance parameters are correctly defined
     *
     * @param parcel the {@link RequestParcel}
     * @return true if the parameters are configured correctly.
     */
    private static boolean checkNuanceConfig(@NonNull final RequestParcel parcel) {
        if (DEBUG) {
            MyLog.i(CLS_NAME, "checkNuanceConfig");
        }

        switch (parcel.getProviderTTS()) {

            case NETWORK_NUANCE:
                if (DEBUG) {
                    MyLog.i(CLS_NAME, "checkNuanceConfig");
                }

                final String NUANCE_APP_KEY = parcel.getNUANCE_APP_KEY();
                final Uri NUANCE_SERVER_URI = parcel.getNUANCE_SERVER_URI();

                if (!UtilsString.notNaked(NUANCE_APP_KEY) || NUANCE_APP_KEY.startsWith(_YOUR_)) {
                    if (DEBUG) {
                        MyLog.e(CLS_NAME, "checkNuanceConfig: API key missing");
                    }
                    return false;
                } else if (!UtilsString.notNaked(NUANCE_SERVER_URI.toString())
                        || NUANCE_SERVER_URI.toString().startsWith(_YOUR_)) {
                    if (DEBUG) {
                        MyLog.e(CLS_NAME, "checkNuanceConfig: URI missing");
                    }
                    return false;
                } else {
                    if (DEBUG) {
                        MyLog.i(CLS_NAME, "checkNuanceConfig: Credentials present");
                    }
                    break;
                }
        }

        switch (parcel.getLanguageModel()) {

            case NUANCE:
                if (DEBUG) {
                    MyLog.i(CLS_NAME, "checkNuanceConfig");
                }

                final String NUANCE_CONTEXT_TAG = parcel.getNUANCE_CONTEXT_TAG();
                final Uri NUANCE_SERVER_URI_NLU = parcel.getNUANCE_SERVER_URI_NLU();

                if (!UtilsString.notNaked(NUANCE_CONTEXT_TAG) || NUANCE_CONTEXT_TAG.startsWith(_YOUR_)) {
                    if (DEBUG) {
                        MyLog.e(CLS_NAME, "checkNuanceConfig: CONTEXT TAG missing");
                    }
                    return false;
                } else if (!UtilsString.notNaked(NUANCE_SERVER_URI_NLU.toString())
                        || NUANCE_SERVER_URI_NLU.toString().startsWith(_YOUR_)) {
                    if (DEBUG) {
                        MyLog.e(CLS_NAME, "checkNuanceConfig: URI missing");
                    }
                    return false;
                } else if (!NUANCE_SERVER_URI_NLU.toString().contains(NuanceConfiguration.SERVER_HOST_NLU)) {
                    if (DEBUG) {
                        MyLog.e(CLS_NAME, "checkNuanceConfig: HOST requires "
                                + NuanceConfiguration.SERVER_HOST_NLU);
                    }
                    return false;
                } else {
                    if (DEBUG) {
                        MyLog.i(CLS_NAME, "checkNuanceConfig: Credentials present");
                    }

                    break;
                }

            default:

                switch (parcel.getProviderVR()) {

                    case NUANCE:
                        if (DEBUG) {
                            MyLog.i(CLS_NAME, "checkNuanceConfig");
                        }

                        final String NUANCE_APP_KEY = parcel.getNUANCE_APP_KEY();
                        final Uri NUANCE_SERVER_URI = parcel.getNUANCE_SERVER_URI();

                        if (!UtilsString.notNaked(NUANCE_APP_KEY) || NUANCE_APP_KEY.startsWith(_YOUR_)) {
                            if (DEBUG) {
                                MyLog.e(CLS_NAME, "checkNuanceConfig: API key missing");
                            }
                            return false;
                        } else if (!UtilsString.notNaked(NUANCE_SERVER_URI.toString())
                                || NUANCE_SERVER_URI.toString().startsWith(_YOUR_)) {
                            if (DEBUG) {
                                MyLog.e(CLS_NAME, "checkNuanceConfig: URI missing");
                            }
                            return false;
                        } else {
                            if (DEBUG) {
                                MyLog.i(CLS_NAME, "checkNuanceConfig: Credentials present");
                            }
                            break;
                        }
                }
        }

        return true;
    }
}