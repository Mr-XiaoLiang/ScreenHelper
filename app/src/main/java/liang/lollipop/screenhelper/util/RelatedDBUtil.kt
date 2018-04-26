package liang.lollipop.screenhelper.util

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.text.TextUtils

/**
 * 关联APP的数据库工具类
 * @author Lollipop
 */
class RelatedDBUtil private constructor(context: Context, dbName:String, factory: SQLiteDatabase.CursorFactory?, version:Int)
    : SQLiteOpenHelper(context,dbName,factory,version)  {

    companion object {

        const val DB_NAME = "LSudoku.db"
        const val VERSION = 1

        fun read(context: Context):DBOperate{
            return DBOperate(RelatedDBUtil(context).readableDatabase)
        }

        fun write(context: Context):DBOperate{
            return DBOperate(RelatedDBUtil(context).writableDatabase)
        }

    }

    private constructor(context: Context):this(context, DB_NAME,null, VERSION)

    private object AppPkg{
        const val TABLE = "APP_PKG_TABLE"
        const val PKG = "PKG_NAME"

        const val CREATE_SQL = "create table $TABLE ( " +
                " $PKG TEXT " +
                " ); "

        const val SELECT_ALL = "SELECT $PKG " +
                " FROM  $TABLE "

        const val SELECT_COUNT = "SELECT $PKG FROM $TABLE WHERE $PKG = ? "

    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(AppPkg.CREATE_SQL)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {  }

    class DBOperate (private val database:SQLiteDatabase){

        fun install(pkg: String): DBOperate{

            val values = ContentValues()
            values.put(AppPkg.PKG,pkg)
            database.insert(AppPkg.TABLE,"",values)

            return this
        }

        fun delete(pkg: String): DBOperate{

            if(!TextUtils.isEmpty(pkg)){
                try {
                    database.delete(AppPkg.TABLE," ${AppPkg.PKG} = ? ",arrayOf(pkg))
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }

            return this
        }

        fun selectAll(pkgList: ArrayList<String>): DBOperate{

            pkgList.clear()

            val cursor = database.rawQuery(AppPkg.SELECT_ALL,null)
            putData(pkgList,cursor)
            cursor.close()

            return this

        }

        /**
         * 整理数据，将数据库数据整理为Bean对象
         */
        private fun putData(list: ArrayList<String>,cursor: Cursor){

            while (cursor.moveToNext()) {

                val pkgName = cursor.getString(cursor.getColumnIndex(AppPkg.PKG))

                list.add(pkgName)

            }
        }

        /**
         * 是否存在
         */
        fun isExist(pkg: String): Boolean{

            return try{
                val cursor = database.rawQuery(AppPkg.SELECT_COUNT, arrayOf(pkg))
                val isExist = cursor.moveToFirst()
                cursor.close()
                isExist
            }catch (e: Exception){
                false
            }

        }

        fun close(){
            database.close()
        }

    }

}