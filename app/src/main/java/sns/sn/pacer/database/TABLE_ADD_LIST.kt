package sns.sn.pacer.database

import android.content.ContentValues
import android.database.Cursor
import android.view.View
import sns.sn.pacer.classes.MyApplication
import sns.sn.pacer.playvideo.model.AddDetails

class TABLE_ADD_LIST
{
    companion object
    {
        val TABLE_NAME : String = "table_TABLE_ADD_LIST"

        val ID : String = "id"
        val ADD_NAME : String = "addName"
        val ADD_TYPE : String = "addType"
        val ADD_ID : String = "addId"
        val RETURN_COUNT : String = "returnCountt"


        var CREATE_NEW_TABLE = ("CREATE TABLE " + TABLE_NAME
                + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ADD_ID + " TEXT, "
                + ADD_NAME + " TEXT, "
                + ADD_TYPE + " TEXT)")


        fun insertAddList(addId: String,addName: String,addType: String)
        {

            val db = MyApplication.db!!.getWritableDatabase()

            val values = ContentValues()
            values.put(ADD_ID, addId)
            values.put(ADD_NAME, addName)
            values.put(ADD_TYPE, addType)

            db.insert(TABLE_NAME, null, values)

        }

        fun selectContentData(): Cursor {

            val db = MyApplication.db!!.getReadableDatabase()

            val cursor: Cursor
            val uQuery = "SELECT DISTINCT $ADD_NAME,$ADD_TYPE FROM $TABLE_NAME"
            cursor = db.rawQuery(uQuery, null)
            return cursor

        }
        fun selectContentData(id : String): Cursor {

            val db = MyApplication.db!!.getReadableDatabase()

            val cursor: Cursor
            val uQuery = "SELECT COUNT(*) AS  '"+RETURN_COUNT+"' FROM $TABLE_NAME WHERE $ID = '"+id+"'"
            cursor = db.rawQuery(uQuery, null)
            return cursor

        }

        fun checkDuplicateAdd(id: String): String {
            var status = "2"
            var cursor: Cursor? = null

            try {


                cursor = selectContentData(id)

                if (cursor!!.moveToNext())
                {
                    val counterCheck = cursor.getString(cursor.getColumnIndex(RETURN_COUNT))

                    val ddd = Integer.valueOf(counterCheck)

                    if (ddd > 0)
                    {
                        status = "1"
                    }
                    else
                    {
                        status = "0"
                    }
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }

            return status
        }
        fun selectContent()  : ArrayList<AddDetails>
        {

            var cursor: Cursor? = null
            val CONTENT_DATA = ArrayList<AddDetails>()
            try
            {
                cursor = selectContentData()
                if (cursor!!.count > 0)
                {
                    while (cursor!!.moveToNext())
                    {
                        val ADD_NAME = cursor!!.getString(cursor.getColumnIndex(ADD_NAME))
                        val ADD_TYPE = cursor.getString(cursor.getColumnIndex(ADD_TYPE))
                        CONTENT_DATA.add(AddDetails(ADD_NAME,ADD_TYPE))
                    }

                }

            }
            catch (e: Exception)
            {

            }
            return CONTENT_DATA

        }
    }
}