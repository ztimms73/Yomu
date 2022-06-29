package org.xtimms.yomu.annotations;

import androidx.annotation.StringDef;

import org.xtimms.yomu.source.Desu;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({
        Desu.CNAME
})
public @interface CName {
}