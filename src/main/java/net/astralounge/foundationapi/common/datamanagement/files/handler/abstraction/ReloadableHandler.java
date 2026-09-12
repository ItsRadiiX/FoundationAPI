package net.astralounge.foundationapi.common.datamanagement.files.handler.abstraction;

import net.astralounge.foundationapi.common.datamanagement.files.handler.abstraction.models.ReloadResult;

public interface ReloadableHandler {
    ReloadResult reload();
}
