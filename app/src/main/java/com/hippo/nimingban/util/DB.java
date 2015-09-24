/*
 * Copyright 2015 Hippo Seven
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

package com.hippo.nimingban.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.hippo.nimingban.client.ac.data.ACForum;
import com.hippo.nimingban.client.data.ACSite;
import com.hippo.nimingban.client.data.CommonPost;
import com.hippo.nimingban.client.data.DisplayForum;
import com.hippo.nimingban.dao.ACCommonPostDao;
import com.hippo.nimingban.dao.ACCommonPostRaw;
import com.hippo.nimingban.dao.ACForumDao;
import com.hippo.nimingban.dao.ACForumRaw;
import com.hippo.nimingban.dao.ACRecordDao;
import com.hippo.nimingban.dao.ACRecordRaw;
import com.hippo.nimingban.dao.DaoMaster;
import com.hippo.nimingban.dao.DaoSession;
import com.hippo.nimingban.dao.DraftDao;
import com.hippo.nimingban.dao.DraftRaw;
import com.hippo.yorozuya.AssertUtils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.LazyList;

public final class DB {

    private static DaoSession sDaoSession;

    public static class DBOpenHelper extends DaoMaster.OpenHelper {

        private boolean mCreate;
        private boolean mUpgrade;
        private int mOldVersion;
        private int mNewVersion;

        public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            super.onCreate(db);
            mCreate = true;
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            mUpgrade = true;
            mOldVersion = oldVersion;
            mNewVersion = newVersion;

            switch (oldVersion) {
                case 1:
                    ACRecordDao.createTable(db, true);
                case 2:
                    ACCommonPostDao.createTable(db, true);
            }
        }

        public boolean isCreate() {
            return mCreate;
        }

        public void clearCreate() {
            mCreate = false;
        }

        public boolean isUpgrade() {
            return mUpgrade;
        }

        public void clearUpgrade() {
            mUpgrade = false;
        }

        public int getOldVersion() {
            return mOldVersion;
        }
    }

    public static void initialize(Context context) {
        DBOpenHelper helper = new DBOpenHelper(
                context.getApplicationContext(), "nimingban", null);

        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);

        sDaoSession = daoMaster.newSession();

        if (helper.isCreate()) {
            helper.clearCreate();

            insertDefaultACForums();
            insertDefaultACCommonPosts();
        }

        if (helper.isUpgrade()) {
            helper.clearUpgrade();

            switch (helper.getOldVersion()) {
                case 2:
                    insertDefaultACCommonPosts();
            }
        }
    }

    private static void insertDefaultACForums() {
        ACForumDao dao = sDaoSession.getACForumDao();
        dao.deleteAll();

        int size = 59;
        String[] ids = {"4", "20", "11", "30", "32", "40", "35", "56", "103", "17", "98",
                "102", "97", "89", "27", "81", "14", "12", "99", "90", "87", "19", "64", "6", "5",
                "93", "101", "2", "73", "72", "86", "22", "70", "95", "10", "34", "51", "44", "23",
                "45", "80", "28", "38", "29", "24", "25", "92", "16", "100", "13", "55", "39", "31",
                "54", "33", "37", "75", "88", "18"};
        String[] names = {"综合版1", "欢乐恶搞", "推理", "技术宅", "料理", "貓版", "音乐", "考试", "文学",
                "二次创作", "姐妹1", "女性向", "女装", "日记", "WIKI", "都市怪谈", "动画", "漫画", "国漫",
                "美漫", "轻小说", "小说", "GALGAME", "VOCALOID", "东方Project", "舰娘", "LoveLive",
                "游戏", "EVE", "DNF", "战争雷霆", "LOL", "DOTA", "GTA5", "Minecraft", "MUG", "WOT",
                "WOW", "D3", "卡牌桌游", "炉石传说", "怪物猎人", "口袋妖怪", "AC大逃杀", "索尼", "任天堂",
                "日麻", "AKB", "SNH48", "COSPLAY", "声优", "模型", "影视", "摄影", "体育", "军武",
                "数码", "天台", "值班室"};
        AssertUtils.assertEquals("ids.size must be size", size, ids.length);
        AssertUtils.assertEquals("names.size must be size", size, names.length);

        for (int i = 0; i < size; i++) {
            ACForumRaw raw = new ACForumRaw();
            raw.setPriority(i);
            raw.setForumid(ids[i]);
            raw.setDisplayname(names[i]);
            raw.setVisibility(true);
            dao.insert(raw);
        }
    }

    private static void insertDefaultACCommonPosts() {
        ACCommonPostDao dao = sDaoSession.getACCommonPostDao();
        dao.deleteAll();

        int size = 13;
        String[] names = {
                "人，是会思考的芦苇", "丧尸图鉴", "壁纸楼", "足控福利", "淡定红茶",
                "胸器福利", "黑妹", "总有一天", "这是芦苇", "赵日天",
                "二次元女友", "什么鬼", "Banner画廊"};
        String[] ids = {
                "6064422", "585784", "117617", "103123", "114373",
                "234446", "55255", "328934", "49607", "1738904",
                "553505", "5739391", "6538597"};
        AssertUtils.assertEquals("ids.size must be size", size, ids.length);
        AssertUtils.assertEquals("names.size must be size", size, names.length);

        for (int i = 0; i < size; i++) {
            ACCommonPostRaw raw = new ACCommonPostRaw();
            raw.setName(names[i]);
            raw.setPostid(ids[i]);
            dao.insert(raw);
        }
    }

    public static List<DisplayForum> getACForums(boolean onlyVisible) {
        ACForumDao dao = sDaoSession.getACForumDao();
        List<ACForumRaw> list = dao.queryBuilder().orderAsc(ACForumDao.Properties.Priority).list();
        List<DisplayForum> result = new ArrayList<>();
        for (ACForumRaw raw : list) {
            if (onlyVisible && !raw.getVisibility()) {
                continue;
            }

            DisplayForum dForum = new DisplayForum();
            dForum.site = ACSite.getInstance();
            dForum.id = raw.getForumid();
            dForum.displayname = raw.getDisplayname();
            dForum.priority = raw.getPriority();
            dForum.visibility = raw.getVisibility();
            result.add(dForum);
        }

        return result;
    }

    public static void setACForums(List<ACForum> list) {
        ACForumDao dao = sDaoSession.getACForumDao();
        dao.deleteAll();

        int i = 0;
        List<ACForumRaw> insertList = new ArrayList<>();
        for (ACForum forum : list) {
            ACForumRaw raw = new ACForumRaw();
            raw.setDisplayname(forum.name);
            raw.setForumid(forum.id);
            raw.setPriority(i);
            raw.setVisibility(true);
            insertList.add(raw);
            i++;
        }

        dao.insertInTx(insertList);
    }

    public static LazyList<ACForumRaw> getACForumLazyList() {
        return sDaoSession.getACForumDao().queryBuilder().orderAsc(ACForumDao.Properties.Priority).listLazy();
    }

    public static void setACForumVisibility(ACForumRaw raw, boolean visibility) {
        raw.setVisibility(visibility);
        sDaoSession.getACForumDao().update(raw);
    }

    public static void updateACForum(Iterable<ACForumRaw> entities) {
        sDaoSession.getACForumDao().updateInTx(entities);
    }

    public static List<DisplayForum> getForums(int site, boolean onlyVisible) {
        // TODO
        return null;
    }

    public static LazyList<DraftRaw> getDraftLazyList() {
        return sDaoSession.getDraftDao().queryBuilder().orderDesc(DraftDao.Properties.Time).listLazy();
    }

    public static void addDraft(String content) {
        addDraft(content, -1);
    }

    public static void addDraft(String content, long time) {
        DraftRaw raw = new DraftRaw();
        raw.setContent(content);
        raw.setTime(time == -1 ? System.currentTimeMillis() : time);
        sDaoSession.getDraftDao().insert(raw);
    }

    public static void removeDraft(long id) {
        sDaoSession.getDraftDao().deleteByKey(id);
    }

    public static final int AC_RECORD_POST = 0;
    public static final int AC_RECORD_REPLY = 1;

    public static LazyList<ACRecordRaw> getACRecordLazyList() {
        return sDaoSession.getACRecordDao().queryBuilder().orderDesc(ACRecordDao.Properties.Time).listLazy();
    }

    public static void addACRecord(int type, String recordid, String postid, String content, String image) {
        addACRecord(type, recordid, postid, content, image, -1);
    }

    public static void addACRecord(int type, String recordid, String postid, String content, String image, long time) {
        ACRecordRaw raw = new ACRecordRaw();
        raw.setType(type);
        raw.setRecordid(recordid);
        raw.setPostid(postid);
        raw.setContent(content);
        raw.setImage(image);
        raw.setTime(time == -1 ? System.currentTimeMillis() : time);
        sDaoSession.getACRecordDao().insert(raw);
    }

    public static void removeACRecord(ACRecordRaw raw) {
        sDaoSession.getACRecordDao().delete(raw);
    }

    public static List<CommonPost> getAllACCommentPost() {
        ACCommonPostDao dao = sDaoSession.getACCommonPostDao();
        List<ACCommonPostRaw> list = dao.queryBuilder().orderAsc(ACCommonPostDao.Properties.Id).list();
        List<CommonPost> result = new ArrayList<>();
        for (ACCommonPostRaw raw : list) {
            CommonPost cp = new CommonPost();
            cp.name = raw.getName();
            cp.id = raw.getPostid();
            result.add(cp);
        }
        return result;
    }

    public static void setACCommonPost(List<CommonPost> list) {
        ACCommonPostDao dao = sDaoSession.getACCommonPostDao();
        dao.deleteAll();

        List<ACCommonPostRaw> insertList = new ArrayList<>();
        for (CommonPost cp : list) {
            ACCommonPostRaw raw = new ACCommonPostRaw();
            raw.setName(cp.name);
            raw.setPostid(cp.id);
            insertList.add(raw);
        }

        dao.insertInTx(insertList);
    }
}
