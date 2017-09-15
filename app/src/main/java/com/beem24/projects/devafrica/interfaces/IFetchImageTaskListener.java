package com.beem24.projects.devafrica.interfaces;


import com.beem24.projects.devafrica.entities.Photo;

import java.util.List;

/**
 * 6/13/2017.
 */

public interface IFetchImageTaskListener {
    void onComplete(List<Photo> photos);
}
