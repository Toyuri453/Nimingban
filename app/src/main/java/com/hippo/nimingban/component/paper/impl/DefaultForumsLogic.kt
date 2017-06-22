/*
 * Copyright 2017 Hippo Seven
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hippo.nimingban.component.paper.impl

import com.hippo.nimingban.NMB_DB
import com.hippo.nimingban.client.data.Forum
import com.hippo.nimingban.component.NmbLogic
import com.hippo.nimingban.component.paper.ForumsLogic
import com.hippo.nimingban.component.paper.ForumsUi
import io.reactivex.android.schedulers.AndroidSchedulers

/*
 * Created by Hippo on 6/22/2017.
 */

class DefaultForumsLogic : NmbLogic(), ForumsLogic {

  var forumsUi: ForumsUi? = null
    set(value) {
      field = value
      value?.onUpdateForums(forums)
    }

  private var forums: List<Forum> = emptyList()

  init {
    NMB_DB.liveForums.observable
        .map { it.toList() }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ onUpdateForums(it) }, { /* Ignore error */ })
        .register()
  }

  private fun onUpdateForums(forums: List<Forum>) {
    this.forums = forums
    forumsUi?.onUpdateForums(forums)
  }
}
