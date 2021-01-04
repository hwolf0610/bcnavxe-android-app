package com.crittermap.backcountrynavigator.xe.share;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.data.model.map.BCMap;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripInfo;
import com.crittermap.backcountrynavigator.xe.ui.account.BCAccountActivity;

import java.util.List;
import java.util.Locale;

import static com.crittermap.backcountrynavigator.xe.share.BCUtils.KEY_SHOW_DOWNLOAD_TUTORIAL;

public class BCAlertDialogHelper {

    public static void showErrorAlert(Context context, BCErrorType errorType, String message) {
        AlertDialog.Builder builder = getBuilder(context);
        switch (errorType) {
            case NETWORK_ERROR:
                builder.setTitle(R.string.notice);
                builder.setMessage(R.string.no_internet);
                builder.setPositiveButton(R.string.tryAgain, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                break;
            case CHANGE_PASSWORD_ERROR:
                builder.setTitle(R.string.notice);
                builder.setMessage(R.string.change_password_error);
                builder.setPositiveButton(R.string.tryAgain, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                break;
            case RESET_PASSWORD_ERROR:
                builder.setTitle(R.string.notice);
                builder.setMessage(message);
                builder.setPositiveButton(R.string.tryAgain, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                break;
            case GPS_NOT_ENABLE:
                builder.setTitle(R.string.warning);
                builder.setMessage(message);
                builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                break;
            case DOWNLOAD_TRIP_ERROR:
                builder.setTitle(R.string.error);
                builder.setMessage(R.string.error_msg_download_failed);
                builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                break;
            case UPLOAD_TRIP_ERROR:
                builder.setTitle(R.string.error);
                builder.setMessage(R.string.error_msg_upload_failed);
                builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                break;
            default:
                builder.setTitle(R.string.warning);
                builder.setMessage(R.string.unknownError);
                builder.setPositiveButton(R.string.tryAgain, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                break;
        }
        builder.create().show();
    }

    public static void showMembershipAlert(final Context context, BCDialogType type, String membershipType) {
        AlertDialog.Builder builder = getBuilder(context);
        switch (type) {
            case MEMBERSHIP_EXPIRED:
                builder.setTitle(R.string.membership_title_expired_warning)
                        .setMessage(R.string.membership_msg_expired_warning)
                        .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(R.string.action_renew, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(context, BCAccountActivity.class);
                                context.startActivity(i);
                            }
                        })
                        .setCancelable(false);
                break;
            case MEMBERSHIP_DOWNGRADE:
                builder.setTitle(R.string.membership_title_downgrade)
                        .setMessage(R.string.membership_msg_downgrade)
                        .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                break;
            case MEMBERSHIP_UPGRADE:
                builder.setTitle(R.string.membership_title_upgrade)
                        .setMessage(context.getString(R.string.membership_msg_upgrade) + " " + membershipType)
                        .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                break;
            case MEMBERSHIP_REQUIRE:
                builder.setTitle(R.string.membership_title_required)
                        .setMessage(R.string.membership_msg_required)
                        .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                break;
        }

        builder.create().show();
    }

    public static void showAlertWithConfirm(Context context, BCDialogType type, DialogInterface.OnClickListener dialogOnClickListener, View view, String message) {
        AlertDialog.Builder builder = getBuilder(context);
        if (view != null) {
            builder.setView(view);
        }
        switch (type) {
            case DOWNLOAD_VECTOR_MAP:
                builder
                        .setTitle("Download Block")
                        .setMessage(message)
                        .setNegativeButton(R.string.btn_cancel, dialogOnClickListener)
                        .setPositiveButton(R.string.btn_ok, dialogOnClickListener);
                break;
            case CONFIRM_DELETE:
                builder
                        .setTitle(R.string.warning)
                        .setMessage(message)
                        .setNegativeButton(R.string.btn_cancel, dialogOnClickListener)
                        .setPositiveButton(R.string.btn_ok, dialogOnClickListener);
                break;
            case UPDATE_MAP:
                builder
                        .setNegativeButton(R.string.btn_cancel, dialogOnClickListener)
                        .setPositiveButton(R.string.btn_ok, dialogOnClickListener);
                break;
            case LOG_OUT:
                builder
                        .setTitle(R.string.warning)
                        .setMessage(message)
                        .setNegativeButton(R.string.btn_cancel, dialogOnClickListener)
                        .setPositiveButton(R.string.btn_ok, dialogOnClickListener);
                break;
            case CONFIRM_SAVE_TRACK:
                builder
                        .setTitle(R.string.track_confirm_title)
                        .setMessage(message)
                        .setCancelable(false)
                        .setNegativeButton(R.string.btn_discard, dialogOnClickListener)
                        .setPositiveButton(R.string.btn_yes, dialogOnClickListener);
                break;
            case CAPTURE_TRIP:
                builder
                        .setTitle(R.string.notice)
                        .setMessage(message)
                        .setNegativeButton(R.string.gallery, dialogOnClickListener)
                        .setPositiveButton(R.string.camera, dialogOnClickListener);
                break;

            case CREATE_TRIP_FOLDER:
                builder
                        .setTitle("Create folder")
                        .setNegativeButton(R.string.btn_cancel, dialogOnClickListener)
                        .setPositiveButton(R.string.btn_create, dialogOnClickListener);
                break;
            case DELETE_MAP:
                String content = String.format(Locale.getDefault(), context.getResources().getString(R.string.confirm_delete_map), message);
                builder
                        .setTitle(R.string.warning)
                        .setMessage(content)
                        .setNegativeButton(R.string.btn_discard, dialogOnClickListener)
                        .setPositiveButton(R.string.btn_delete, dialogOnClickListener);
                break;
            case NEED_LOGIN:
                builder
                        .setTitle(R.string.warning)
                        .setMessage(R.string.need_login)
                        .setNegativeButton(R.string.btn_cancel, dialogOnClickListener)
                        .setPositiveButton(R.string.btn_do_login, dialogOnClickListener);
                break;
            case SAVE_DRAWING:
                builder
                        .setTitle(R.string.save)
                        .setMessage(R.string.save_drawing_confirm)
                        .setNegativeButton(R.string.btn_cancel, dialogOnClickListener)
                        .setPositiveButton(R.string.btn_yes, dialogOnClickListener);
                break;


            case EXIT:
                builder.setTitle(R.string.warning)
                        .setMessage(R.string.msg_exit)
                        .setNegativeButton(R.string.btn_cancel, dialogOnClickListener)
                        .setPositiveButton(R.string.btn_yes, dialogOnClickListener);
            case NEED_SELECT_TRIP:
                builder.setTitle(R.string.warning)
                        .setMessage(R.string.need_select_trip)
                        .setNegativeButton(R.string.btn_cancel, null)
                        .setPositiveButton(R.string.btn_yes, dialogOnClickListener);
        }
        builder.create().show();
    }

    public static <T> void showTripActionDialog(Context context, BCDialogType type
            , final DialogInterface.OnClickListener onDialogButtonClick, T data) {
        AlertDialog.Builder builder = getBuilder(context);
        switch (type) {
            case PIN_TRIP_NOT_ALLOW:
                builder.setTitle(R.string.title_alert_pinned_trip)
                        .setMessage(R.string.msg_trip_download)
                        .setNegativeButton(R.string.btn_cancel, null)
                        .setPositiveButton(R.string.btn_ok, onDialogButtonClick);
                break;
            case PIN_TRIP_ALERT:
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialogView = inflater.inflate(R.layout.alert_dialog_pin_trip, null);
                final RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroup);
                List<BCTripInfo> pinnedTrips = (List<BCTripInfo>) data;
                for (BCTripInfo pinTrip : pinnedTrips) {
                    RadioButton radioButton = new RadioButton(context);
                    radioButton.setText(pinTrip.getName());
                    radioButton.setId((pinnedTrips.indexOf(pinTrip)));
                    radioButton.setTextAppearance(context, R.style.PinnedRadioButtonAppearance);
                    radioGroup.addView(radioButton);
                }

                builder.setTitle(R.string.title_alert_pinned_trip)
                        .setView(dialogView)
                        .setNegativeButton(R.string.btn_cancel, null)
                        .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (onDialogButtonClick != null)
                                    onDialogButtonClick.onClick(dialog, radioGroup.getCheckedRadioButtonId());
                            }
                        });
                break;
            case CONFIRM_DOWNLOAD_TRIP:
                //TODO check trip size @Trang
                builder.setTitle(R.string.title_download)
                        .setMessage(R.string.mgs_confirm_download)
                        .setNegativeButton(R.string.btn_cancel, null)
                        .setPositiveButton(R.string.btn_yes, onDialogButtonClick);
                break;
            case SYNC_REDOWNLOAD_TRIP:
                builder.setTitle(R.string.item_trip_action_redownload)
                        .setMessage(R.string.msg_sync_redownload)
                        .setNegativeButton(R.string.btn_cancel, onDialogButtonClick)
                        .setPositiveButton(R.string.btn_yes, onDialogButtonClick);
                break;
            case SYNC_UPLOAD_TRIP:
                builder.setTitle(R.string.item_trip_action_upload)
                        .setMessage(R.string.msg_sync_upload)
                        .setNegativeButton(R.string.btn_cancel, onDialogButtonClick)
                        .setPositiveButton(R.string.btn_yes, onDialogButtonClick);
                break;
            case SYNC_CONFLICT_TRIP:
                builder.setTitle(R.string.title_conflict)
                        .setMessage(R.string.msg_conflict)
                        .setNegativeButton(R.string.item_trip_action_redownload, onDialogButtonClick)
                        .setPositiveButton(R.string.item_trip_action_upload, onDialogButtonClick);
                break;
            case CONFIRM_DELETE_TRIP:
                builder.setTitle("Delete Trip")
                        .setMessage(String.format("Are you sure to delete \"%s\" trip?", data))
                        .setNegativeButton(R.string.btn_discard, null)
                        .setPositiveButton(R.string.btn_delete, onDialogButtonClick);
        }

        builder.create().show();
    }


    public static void showCreateFolderDialog(final Context context, final OnCreateFolderListener onCreateFolderListener) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_create_folder, null);
        final EditText etFolder = dialogView.findViewById(R.id.et_folder);
        final TextInputLayout textInputLayout = dialogView.findViewById(R.id.til_folder);


        final AlertDialog dialog = getBuilder(context)
                .setTitle(R.string.title_create_folder)
                .setView(dialogView).create();

        dialogView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialogView.findViewById(R.id.btn_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newFolder = etFolder.getText().toString().trim();
                if (TextUtils.isEmpty(newFolder)) {
                    textInputLayout.setErrorEnabled(true);
                    textInputLayout.setError(context.getString(R.string.error_no_folder_name));

                } else {
                    if (onCreateFolderListener != null) onCreateFolderListener.onCreate(newFolder);
                    dialog.dismiss();
                }
            }
        });

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.show();

    }

    public static <T> void showMapActionDialog(Context context, BCDialogType type
            , final DialogInterface.OnClickListener onPositiveClick, T data) {
        AlertDialog.Builder builder = getBuilder(context);
        switch (type) {
//            case PIN_MAP_NOT_ALLOW:
//                builder.setTitle(R.string.title_alert_pinned_map)
//                        .setMessage(R.string.msg_map_download)
//                        .setNegativeButton(R.string.btn_cancel, null)
//                        .setPositiveButton(R.string.btn_ok, onPositiveClick);
//                break;
            case PIN_MAP_ALERT:
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialogView = inflater.inflate(R.layout.alert_dialog_pin_map, null);
                final RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroup);
                List<BCMap> pinnedMaps = (List<BCMap>) data;
                for (BCMap pinMap : pinnedMaps) {
                    RadioButton radioButton = new RadioButton(context);
                    radioButton.setText(pinMap.getMapName());
                    radioButton.setId((pinnedMaps.indexOf(pinMap)));
                    radioButton.setTextAppearance(context, R.style.PinnedRadioButtonAppearance);
                    radioGroup.addView(radioButton);
                }

                builder.setTitle(R.string.pin_map)
                        .setView(dialogView)
                        .setNegativeButton(R.string.btn_cancel, null)
                        .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onPositiveClick.onClick(dialog, radioGroup.getCheckedRadioButtonId());
                            }
                        });
                break;
        }

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.colorPrimary));
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.colorPrimary));
    }

    public static void showNeedLoginAlert(final Activity activity, Context c) {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case DialogInterface.BUTTON_POSITIVE:
                        BCUtils.goToMain(activity);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialogInterface.dismiss();
                        break;
                }
            }
        };
        showAlertWithConfirm(c, BCDialogType.NEED_LOGIN, listener, null, null);
    }

    public static void showDownloadMapTutorial(Context context, final View.OnClickListener closeOnClick) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_download_map_tutorial, null);

        final AlertDialog dialog = getBuilder(context)
                .setView(dialogView).create();

        dialogView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (closeOnClick != null)
                    closeOnClick.onClick(v);
            }
        });

        CheckBox checkBox = dialogView.findViewById(R.id.checkBox_show);
        checkBox.setChecked(BCUtils.getSharedPrefereneceBoolean(KEY_SHOW_DOWNLOAD_TUTORIAL));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                BCUtils.saveSharedPreferences(KEY_SHOW_DOWNLOAD_TUTORIAL, isChecked);
            }
        });

        dialog.show();
    }

    public static void showAlertDialogBuider(Context context, BCAlertType alertType, String message, DialogInterface.OnClickListener onSave, DialogInterface.OnClickListener onCancel) {
        AlertDialog.Builder builder = getBuilder(context);
        switch (alertType) {
            case WARNING:
                builder.setTitle(context.getString(R.string.warning));
                builder.setMessage(message);
                builder.setPositiveButton("Save", onSave);
                builder.setNegativeButton("Discard", onCancel);
                break;
            case INFORMATION:
                builder.setTitle(context.getString(R.string.information));
                builder.setMessage(message);
                builder.setPositiveButton("Download", onSave);
                builder.setNegativeButton("Cancel", onCancel);
                break;
            case INCORRECT_PASSWORD:
                builder.setTitle(context.getString(R.string.incorrectPassword));
                builder.setMessage(message);
                builder.setPositiveButton(R.string.tryAgain, onSave);
                break;
            case INVALID_EMAIL:
                builder.setTitle(context.getString(R.string.emailExist));
                builder.setMessage(message);
                builder.setPositiveButton(R.string.tryAgain, onSave);
                break;
            case SHOW_RESTART_APP:
                builder.setTitle("Restart App");
                builder.setMessage(message);
                builder.setPositiveButton("Ok", onSave);
                builder.setNegativeButton("Cancel", onCancel);
                break;
        }
        builder.create().show();
    }

    public static AlertDialog buildMapAlertDialog(Context context, View v, final Runnable onClickListener) {
        AlertDialog.Builder builder = getBuilder(context);
        builder.setView(v);
        final AlertDialog dialog = builder.create();

        Button btnCancel = v.findViewById(R.id.btn_cancel_grid_size);
        Button btnOk = v.findViewById(R.id.btn_ok_grid_size);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.run();
                dialog.dismiss();
            }
        });
        return dialog;
    }

    public enum BCDialogType {
        // Membership
        MEMBERSHIP_EXPIRED,
        MEMBERSHIP_DOWNGRADE,
        MEMBERSHIP_UPGRADE,
        MEMBERSHIP_REQUIRE,
        REQUIRE,

        // Map
        UPDATE_MAP,

        // Generic dialog
        CONFIRM_DELETE,
        CONFIRM_SAVE_TRACK,
        CREATE_TRIP_FOLDER,

        // Logout
        LOG_OUT,

        //Trip
        PIN_TRIP_NOT_ALLOW,
        PIN_TRIP_ALERT,
        CAPTURE_TRIP,
        CONFIRM_DOWNLOAD_TRIP,
        SYNC_CONFLICT_TRIP,
        SYNC_REDOWNLOAD_TRIP,
        SYNC_UPLOAD_TRIP,
        CONFIRM_DELETE_TRIP,

        //Map
        PIN_MAP_NOT_ALLOW,
        PIN_MAP_ALERT,
        NEED_LOGIN,
        DELETE_MAP,
        //Map


        SAVE_DRAWING,
        NEED_SELECT_TRIP, EXIT,
        DOWNLOAD_VECTOR_MAP
    }

    public interface OnCreateFolderListener {
        void onCreate(String folderName);
    }

    private static AlertDialog.Builder getBuilder(Context context) {
        return new AlertDialog.Builder(context,R.style.MyAlertDialogTheme);
    }
}
