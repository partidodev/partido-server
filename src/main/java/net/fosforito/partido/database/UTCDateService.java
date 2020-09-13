package net.fosforito.partido.database;

import java.util.Date;

/**
 * Basic interface for UTC Date Service management.
 */
public interface UTCDateService {

    /**
     * @return date according to UTC, since Java's instant is based off of the UTC timezone.
     */
    public Date getConvertedUTCDate();
}
