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

import com.hippo.nimingban.NMB_CLIENT
import com.hippo.nimingban.R
import com.hippo.nimingban.client.data.Forum
import com.hippo.nimingban.client.data.Reply
import com.hippo.nimingban.client.data.Thread
import com.hippo.nimingban.component.NmbLogic
import com.hippo.nimingban.component.NmbScene
import com.hippo.nimingban.component.paper.ThreadsLogic
import com.hippo.nimingban.component.scene.galleryScene
import com.hippo.nimingban.component.scene.repliesScene
import com.hippo.nimingban.exception.PresetException
import com.hippo.nimingban.widget.content.ContentData
import com.hippo.nimingban.widget.content.ContentDataAdapter
import com.hippo.nimingban.widget.content.ContentLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/*
 * Created by Hippo on 6/19/2017.
 */

open class DefaultThreadsLogic(
    val scene: NmbScene
) : NmbLogic(), ThreadsLogic {

  private val data = ThreadsData()

  var forum: Forum? = null
    set(value) {
      val oldValue = field
      field = value
      // Refresh if the forum id is different or null
      if (value?.id != oldValue?.id || value?.id == null) {
        data.goTo(0)
      }
    }

  init {
    data.forceProgress()
  }

  override fun initializeAdapter(adapter: ContentDataAdapter<Thread, *>) {
    adapter.data = data
  }

  override fun terminateAdapter(adapter: ContentDataAdapter<Thread, *>) {
    adapter.data = null
  }

  override fun initializeContentLayout(contentLayout: ContentLayout) {
    data.ui = contentLayout
  }

  override fun terminateContentLayout(contentLayout: ContentLayout) {
    data.ui = null
  }

  override fun onClickThread(thread: Thread) {
    scene.stage?.pushScene(thread.repliesScene())
  }

  override fun onClickThumb(reply: Reply) {
    scene.stage?.pushScene(reply.galleryScene())
  }


  inner class ThreadsData : ContentData<Thread>() {

    override fun onRequireData(id: Int, page: Int) {
      val forum = this@DefaultThreadsLogic.forum
      if (forum != null) {
        NMB_CLIENT.threads(forum.id, page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ setData(id, it, Int.MAX_VALUE) }, { setError(id, it) })
            .register()
      } else {
        schedule { setError(id, PresetException("No forum", R.string.error_no_forum, R.drawable.emoticon_sad_primary_x64)) }
      }
    }

    override fun onRestoreData(id: Int) {
      // TODO("not implemented")
    }

    override fun onBackupData(data: List<Thread>) {
      // TODO("not implemented")
    }

    override fun isDuplicate(t1: Thread, t2: Thread) = t1.id == t2.id
  }
}