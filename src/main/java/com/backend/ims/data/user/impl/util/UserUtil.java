package com.backend.ims.data.user.impl.util;

import com.backend.ims.data.user.api.common.UserCommon;
import com.backend.ims.data.user.api.model.User;

public class UserUtil {

  public static int compare(User u1, User u2) {
    return u1.getRoles().contains(UserCommon.ADMIN) ? -1 : u2.getRoles().contains(UserCommon.ADMIN) ? 1 : 0;
  }

  public static void hideSensitiveData(User user) {
    user.setPassword(null);
  }
}
