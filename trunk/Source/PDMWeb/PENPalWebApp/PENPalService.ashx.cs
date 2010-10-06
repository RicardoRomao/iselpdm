using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Data;
using System.Data.SqlClient;
using System.Configuration;
using System.Text;

namespace PENPalWebApp
{
	public class PENPalService : IHttpHandler
	{
		public void ProcessRequest(HttpContext context)
		{
			string action = context.Request["action"];
			string resp = string.Empty;

			switch (action)
			{
				case "add":
					resp = AddItem(context);
					break;
				case "list":
					resp = ListItems(context);
					break;
				default:
					break;
			}

			context.Response.ContentType = "text/plain";
			context.Response.AppendHeader("Content-Length", resp.Length.ToString());
			context.Response.Write(resp);
		}

		private string ListItems(HttpContext ctx)
		{
			SqlConnection con = new SqlConnection(ConfigurationManager.ConnectionStrings["bd"].ConnectionString);
			SqlCommand cmd = con.CreateCommand();
			SqlDataReader reader;
			String query;
			SqlParameter param;
			String ret;
			Int32 numReg = 0;
			StringBuilder sb = new StringBuilder(25);
			// |
			// id, categoryId, Title, Description, ExpiryDate

			query = "SELECT * FROM Item WHERE UserId = @UserId";

			cmd.CommandText = query;
			cmd.CommandType = CommandType.Text;

			// User Id
			param = new SqlParameter("@UserId", SqlDbType.Int);
			param.Value = ctx.Request["user"];
			cmd.Parameters.Add(param);

			con.Open();
			reader = cmd.ExecuteReader();
			while (reader.Read())
			{
				if (sb.Length > 0)
				{
					sb.Append("#");
				}
				sb.Append(reader.GetInt32(0));
				sb.Append("|");
				sb.Append(reader.GetInt32(6));
				sb.Append("|");
				sb.Append(reader.GetString(1));
				sb.Append("|");
				sb.Append(reader.GetString(2));
				sb.Append("|");
				sb.Append(reader.GetInt32(4));
				numReg++;
			}
			reader.Close();
			con.Close();

			cmd.Dispose();
			con.Dispose();

			sb.Insert(0, numReg + "«");
			ret = sb.ToString();
			sb.Length = 0;

			return ret;
		}

		private string AddItem(HttpContext ctx)
		{
			SqlConnection con = new SqlConnection(ConfigurationManager.ConnectionStrings["bd"].ConnectionString);
			SqlCommand cmd = con.CreateCommand();
			String query;
			SqlParameter param;
			String ret;

			query = "INSERT INTO Item(Title, Description, ExpiryDate, UserId, CategoryId) VALUES(@Title, @Description, @ExpiryDate, @UserId, @CategoryId)" +
					"SELECT @@IDENTITY";

			cmd.CommandText = query;
			cmd.CommandType = CommandType.Text;

			// Title
			param = new SqlParameter("@Title", SqlDbType.VarChar);
			param.Value = ctx.Request["title"];
			cmd.Parameters.Add(param);

			// Description
			param = new SqlParameter("@Description", SqlDbType.Text);
			param.Value = ctx.Request["description"];
			cmd.Parameters.Add(param);

			// Expiry Date
			param = new SqlParameter("@ExpiryDate", SqlDbType.Int);
			param.Value = ctx.Request["expirydate"];
			cmd.Parameters.Add(param);

			// User Id
			param = new SqlParameter("@UserId", SqlDbType.Int);
			param.Value = ctx.Request["user"];
			cmd.Parameters.Add(param);

			// Category Id
			param = new SqlParameter("@CategoryId", SqlDbType.Int);
			param.Value = ctx.Request["category"];
			cmd.Parameters.Add(param);

			con.Open();
			ret = cmd.ExecuteScalar().ToString();
			con.Close();

			cmd.Dispose();
			con.Dispose();

			return ret;
		}

		public bool IsReusable
		{
			get
			{
				return false;
			}
		}
	}
}
