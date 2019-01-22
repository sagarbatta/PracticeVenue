package com.venue.rest.dao;

import java.net.URI;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.venue.rest.model.Menu;
import com.venue.rest.model.SubMenu;
import com.venue.rest.util.ErrorMessage;
@Repository
public class MenuDAO {
	
	private static Logger logger = Logger.getLogger(MenuDAO.class);
	String errorCode = "";
	private JdbcTemplate jdbcTemplateObject = null;
	private RestTemplate restTemplate;
	
	
	@Autowired
	@Qualifier("dataSourceVenue")
	DataSource dataSourceVenue;
	
	MenuDAO (){}
	@PostConstruct
    public void init() {
		jdbcTemplateObject = new JdbcTemplate(dataSourceVenue);	
		this.restTemplate = new RestTemplate();;
    }
	MenuDAO(JdbcTemplate jdbcTemplateObject, RestTemplate restTemplate) {
		this.jdbcTemplateObject = jdbcTemplateObject;
		this.restTemplate = restTemplate;
	}
	
	/**
	 * Method to intialize DB
	 */
	@SuppressWarnings("resource")
	private void initializeDB() {
		try {
			ApplicationContext context = new ClassPathXmlApplicationContext("venue_db.xml");
			DataSource dataSource = (DataSource) context.getBean("dataSourceVenue");
			this.jdbcTemplateObject = new JdbcTemplate(dataSource);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to get Menu List response
	 */
	public Object getMenuList(String appUserId,String appId,String venueId) {
		logger.info("::in getMenuList DAO::" +appUserId);
		logger.info("::appUserId::" +appUserId);
		logger.info("::appId::" +appId);
		logger.info("::venueId::" +venueId);
		if(appUserId != null && !appUserId.equalsIgnoreCase("")){
			Object menuList = getMenuListResponse(appUserId,appId,venueId);
			if(menuList != null){
				return menuList;
			} else {
				errorCode = "500";
				return ErrorMessage.getInstance().getErrorResponse(errorCode);
			}
		} else {
			errorCode = "1002";
			return ErrorMessage.getInstance().getErrorResponse(errorCode);
		}
	}
	
	/**
	 * Method to get Menu List response
	 */
	public Object getMenuList(String appUserId,String appId,String venueId, ArrayList<HashMap<Object, Object>> userCurrentPlaces, String segmentsUrl) {
		logger.info("::in getMenuList DAO::" +appUserId);
		logger.info("::appUserId::" +appUserId);
		logger.info("::appId::" +appId);
		logger.info("::venueId::" +venueId);
		if(appUserId != null && !appUserId.equalsIgnoreCase("")){
			Object menuList = getMenuListResponse(appUserId,appId,venueId,"",userCurrentPlaces, segmentsUrl);
			if(menuList != null){
				return menuList;
			} else {
				errorCode = "500";
				return ErrorMessage.getInstance().getErrorResponse(errorCode);
			}
		} else {
			errorCode = "1002";
			return ErrorMessage.getInstance().getErrorResponse(errorCode);
		}
	}
	
	/**
	 * Method to get Menu List response
	 */
	public Object getMenuList(String appUserId,String appId,String venueId, String menuId, ArrayList<HashMap<Object, Object>> userCurrentPlaces, String segmentsUrl) {
		logger.info("::in getMenuList DAO::" +appUserId);
		logger.info("::appUserId::" +appUserId);
		logger.info("::appId::" +appId);
		logger.info("::venueId::" +venueId);
		if(appUserId != null && !appUserId.equalsIgnoreCase("")){
			Object menuList = getMenuListResponse(appUserId,appId,venueId,menuId,userCurrentPlaces,segmentsUrl);
			if(menuList != null){
				return menuList;
			} else {
				errorCode = "500";
				return ErrorMessage.getInstance().getErrorResponse(errorCode);
			}
		} else {
			errorCode = "1002";
			return ErrorMessage.getInstance().getErrorResponse(errorCode);
		}
	}
	
	/**
	 * Method to get Menu List from DB
	 */
	private Object getMenuListResponse(String appUserId, String appId, String venueId) {
		logger.info("::in getMenuListResponse::");
		ArrayList<Menu> menuList = new ArrayList<Menu>();
		ArrayList<SubMenu> subMenuList = null;
		HashMap<String, Object> menuMap = new HashMap<String, Object>();
		String lastUpdateTime = "";
 		SqlRowSet rst = null,rst1 = null;
 		
		try {
			rst = jdbcTemplateObject.queryForRowSet("select updated_time from tbl_hub_menu_last_updated where venue_id=? and app_id=?",
					new Object[]{venueId,appId});
			while(rst.next()) {
				lastUpdateTime = rst.getString("updated_time");
				logger.info("::lastUpdateTime::" +lastUpdateTime);
			}
			rst = jdbcTemplateObject.queryForRowSet("select * from tbl_hub_menu_list where venue_id=? and app_id=? order by sort_order_id",
					new Object[]{venueId,appId});
			while(rst.next()) {
				Menu menu = new Menu();
				menu.setMenu_id(rst.getString("menu_id"));
				menu.setKey(rst.getString("menu_key"));
				menu.setValue(rst.getString("value"));
				menu.setMenu_view_type(rst.getString("menu_view_type"));
				menu.setIcon(rst.getString("icon"));
				menu.setSelected_icon(rst.getString("selected_icon"));
				menu.setDeeplink_url(rst.getString("deeplink_url"));
				menu.setSort_order_id(rst.getString("sort_order_id"));
				
				rst1 = jdbcTemplateObject.queryForRowSet("select * from tbl_hub_sub_menu_list where menu_id="+rst.getInt("menu_id")+" order by sort_order_id");
				if(rst1 != null) {
					subMenuList = new ArrayList<SubMenu>();
				}
				while(rst1.next()) {
					SubMenu subMenu = new SubMenu();
					subMenu.setSub_menu_id(rst1.getString("sub_menu_id"));
					subMenu.setKey(rst1.getString("menu_key"));
					subMenu.setValue(rst1.getString("value"));
					subMenu.setIcon(rst1.getString("icon"));
					subMenu.setSelected_icon(rst1.getString("selected_icon"));
					subMenu.setDeeplink_url(rst1.getString("deeplink_url"));
					subMenu.setSort_order_id(rst1.getString("sort_order_id"));
					subMenu.setWeb_url(rst1.getString("web_url"));
					subMenu.setSignup_required(rst1.getString("signup_required"));
					subMenu.setIcon_2x(rst1.getString("icon_2x"));
					subMenu.setSelected_icon_2x(rst1.getString("selected_icon_2x"));
					subMenu.setWidth(rst1.getString("menu_width"));
					subMenu.setHeight(rst1.getString("menu_height"));
					subMenuList.add(subMenu);
				}
				if(subMenuList!=null){
					menu.setSubMenuList(subMenuList);
				}
				menuList.add(menu);
			}
			menuMap.put("lastupdatedtime", lastUpdateTime);
			menuMap.put("menulist", menuList);
		} catch(Exception e) {
			logger.info("::Exception in getMenuListResponse::"+e.getLocalizedMessage());
			e.printStackTrace();
		}
		return menuMap;
	}
	
	/**
	 * Method to get Menu List from DB
	 */
	private Object getMenuListResponse(String appUserId, String appId, String venueId, String menuId, ArrayList<HashMap<Object, Object>> userCurrentPlaces, String segmentsUrl) {
		logger.info("::in getMenuListResponse::");
		ArrayList<Menu> menuList = new ArrayList<Menu>();
		ArrayList<SubMenu> subMenuList = null;
		HashMap<String, Object> menuMap = new HashMap<String, Object>();
		String lastUpdateTime = "";
 		SqlRowSet rst = null, rst1 = null, rst2 = null;
 		String subMenuId = "";
 		String menuCalendarId = "";
 		String subMenuCalendarId = "";
 		
		try {
			rst = jdbcTemplateObject.queryForRowSet("select updated_time from tbl_hub_menu_last_updated where venue_id=? and app_id=?",
					new Object[]{venueId,appId});
			while(rst.next()) {
				lastUpdateTime = rst.getString("updated_time");
				logger.info("::lastUpdateTime::" +lastUpdateTime);
			}
			String emp2UserId = geteMp2UserId(appUserId);
			/*rst = jdbcTemplateObject.queryForRowSet("select * from tbl_hub_menu_list where venue_id=? and app_id=? and UTC_TIMESTAMP() BETWEEN menu_start_time AND menu_end_time and menu_status='publish' order by sort_order_id",
					new Object[]{venueId,appId});*/
			
			logger.info("::menuId::" +menuId);
			if(menuId != null && !menuId.equals(""))
				rst = jdbcTemplateObject.queryForRowSet("select * from tbl_hub_menu_list where venue_id=? and app_id=? and menu_id=? and ((UTC_TIMESTAMP() BETWEEN menu_start_time AND menu_end_time) OR (menu_start_time='0000-00-00 00:00:00' and menu_end_time='0000-00-00 00:00:00') OR (UTC_TIMESTAMP() > menu_start_time and menu_end_time='0000-00-00 00:00:00')) and menu_status='publish' order by sort_order_id",
						new Object[]{venueId,appId,menuId});
			else
				rst = jdbcTemplateObject.queryForRowSet("select * from tbl_hub_menu_list where venue_id=? and app_id=? and ((UTC_TIMESTAMP() BETWEEN menu_start_time AND menu_end_time) OR (menu_start_time='0000-00-00 00:00:00' and menu_end_time='0000-00-00 00:00:00') OR (UTC_TIMESTAMP() > menu_start_time and menu_end_time='0000-00-00 00:00:00')) and menu_status='publish' order by sort_order_id",
						new Object[]{venueId,appId});
			while(rst.next()) {
				menuId = rst.getString("menu_id");
				menuCalendarId = rst.getString("menu_calendar_id");
				if(checkPlaceAndAudienceSegments(menuId, appUserId, userCurrentPlaces, "menu", menuCalendarId, segmentsUrl, emp2UserId)) {
					Menu menu = new Menu();
					menu.setMenu_id(menuId);
					menu.setKey(rst.getString("menu_key"));
					menu.setValue(rst.getString("value"));
					
					rst2= jdbcTemplateObject.queryForRowSet("select name from tbl_menu_view_type where id=?",new Object[]{rst.getString("menu_view_type")});
					if(rst2 != null && rst2.next())
						menu.setMenu_view_type(rst2.getString("name"));
					else
						menu.setMenu_view_type("");
				    
					if(rst.getString("icon") != null && !rst.getString("icon").equals(""))
						menu.setIcon(rst.getString("icon"));
					else
						menu.setIcon("");
					
					if(rst.getString("selected_icon") != null && !rst.getString("selected_icon").equals(""))
						menu.setSelected_icon(rst.getString("selected_icon"));
					else
						menu.setSelected_icon("");
					
					if(rst.getString("deeplink_url") != null && !rst.getString("deeplink_url").equals(""))
						menu.setDeeplink_url(rst.getString("deeplink_url"));
					else
						menu.setDeeplink_url("");
					menu.setSort_order_id(rst.getString("sort_order_id"));
					
					//rst1 = jdbcTemplateObject.queryForRowSet("select * from tbl_hub_sub_menu_list where menu_id="+rst.getInt("menu_id")+" and UTC_TIMESTAMP() BETWEEN menu_start_time AND menu_end_time and sub_menu_status='publish' order by sort_order_id");
					rst1 = jdbcTemplateObject.queryForRowSet("select * from tbl_hub_sub_menu_list where menu_id="+rst.getInt("menu_id")+" and ((UTC_TIMESTAMP() BETWEEN menu_start_time AND menu_end_time) OR (menu_start_time='0000-00-00 00:00:00' and menu_end_time='0000-00-00 00:00:00') OR (UTC_TIMESTAMP() > menu_start_time and menu_end_time='0000-00-00 00:00:00')) and sub_menu_status='publish' order by sort_order_id");
					if(rst1 != null) {
						subMenuList = new ArrayList<SubMenu>();
					}
					while(rst1.next()) {
						subMenuId = rst1.getString("sub_menu_id");
						subMenuCalendarId = rst1.getString("sub_menu_calendar_id");
						if(checkPlaceAndAudienceSegments(subMenuId, appUserId, userCurrentPlaces, "subMenu", subMenuCalendarId, segmentsUrl, emp2UserId)) {
							SubMenu subMenu = new SubMenu();
							subMenu.setSub_menu_id(subMenuId);
							subMenu.setKey(rst1.getString("menu_key"));
							subMenu.setValue(rst1.getString("value"));
							subMenu.setIcon(rst1.getString("icon"));
							subMenu.setSelected_icon(rst1.getString("selected_icon"));
							subMenu.setDeeplink_url(rst1.getString("deeplink_url"));
							subMenu.setSort_order_id(rst1.getString("sort_order_id"));
							subMenu.setWeb_url(rst1.getString("web_url"));
							subMenu.setSignup_required(rst1.getString("signup_required"));
							subMenu.setIcon_2x(rst1.getString("icon_2x"));
							subMenu.setSelected_icon_2x(rst1.getString("selected_icon_2x"));
							subMenu.setWidth(rst1.getString("menu_width"));
							subMenu.setHeight(rst1.getString("menu_height"));
							subMenuList.add(subMenu);
						}
					}
					if(subMenuList!=null){
						menu.setSubMenuList(subMenuList);
					}
					menuList.add(menu);
				}
			}
			menuMap.put("lastupdatedtime", lastUpdateTime);
			menuMap.put("menulist", menuList);
		} catch(Exception e) {
			logger.info("::Exception in getMenuListResponse::"+e.getLocalizedMessage());
			e.printStackTrace();
		}
		return menuMap;
	}
	
	public boolean checkPlaceAndAudienceSegments (String id, String appUserId, ArrayList<HashMap<Object, Object>> userCurrentPlaces, 
			String menuType, String calendarId, String segmentsUrl, String emp2UserId) {
		logger.info("::in checkPlaceAndAudienceSegments::");
		SqlRowSet rst = null, rst1 = null, rst2 = null, rst3 = null;
		int placeSegmentId = 0;
		int audienceSegmentId = 0;
		boolean positivePlaceRestriction = false;
		boolean negativePlaceRestriction = false;
		boolean audienceRestriction = false;
		boolean timeRestriction = false;
		boolean timeMatched = true;
		boolean positivePlaceMatched = false;
		boolean negativePlaceMatched = true;
		boolean audienceMatched = false;
		boolean userMatched = true;
		String placeSegmentName = "";
		String startDateTime = "";
		String endDateTime = "";
		String repeatType = "";
		String repeatWeeklyOn = "";
		if (userCurrentPlaces == null) {
			userCurrentPlaces = new ArrayList();
		}
		
		try {
			logger.info("::id::"+ id);
			logger.info("::menuType::"+ menuType);
			//check for place restriction
			rst = jdbcTemplateObject.queryForRowSet("select * from tbl_menu_place_map where id=? and menu_type=?",
					new Object[]{id, menuType});
			while(rst.next()) {
				placeSegmentId = rst.getInt("place_segment_id");
				placeSegmentName = rst.getString("place_segment_name");
				if(placeSegmentId > 0 && placeSegmentName != null && !placeSegmentName.equals("")) {
					if (rst.getInt("negative") != 1) {
						positivePlaceRestriction = true;
						for (HashMap<Object, Object> location : userCurrentPlaces) {
							if (placeSegmentName.equalsIgnoreCase(location.get("placeName").toString())) {
								positivePlaceMatched = true;
							}
						}
					}
					else if(rst.getInt("negative") == 1) {
						negativePlaceRestriction = true;
						boolean matched = false;
						for (HashMap<Object, Object> location : userCurrentPlaces) {
							if (placeSegmentName.equalsIgnoreCase(location.get("placeName").toString())) {
								matched = true;
							}
						}
						if (matched) {
							negativePlaceMatched =false;
						}
					}
				}
			}

			//check for audience restriction
			rst1 = jdbcTemplateObject.queryForRowSet("select * from tbl_menu_audience_map where id=? and menu_type=?",
					new Object[]{id, menuType});
			while(rst1.next()) {
				audienceSegmentId = rst1.getInt("audience_segment_id");
				logger.info("::audienceSegmentId::" +audienceSegmentId);
				if(audienceSegmentId > 0) {
					logger.info("::audienceSegmentId is available::");
					audienceRestriction = true;
					
					//START - Call REST API in eMcards to verify if user belongs to AppBoy Segment
					ObjectMapper mapperObj = new ObjectMapper();
		    		Map<String, Object> inputMap = new HashMap<String, Object>();
		    		inputMap.put("audienceSegmentId", audienceSegmentId);
		    		inputMap.put("emp2UserId", emp2UserId);
		    		String body = mapperObj.writeValueAsString(inputMap);
		    		
					HttpHeaders httpHeaders = new HttpHeaders();
			    	httpHeaders.setContentType(MediaType.APPLICATION_JSON);
			    	HttpEntity<String> entity = new HttpEntity<String>(body, httpHeaders);
			    	
			    	//send request
			    	audienceMatched = restTemplate.postForObject(new URI(segmentsUrl), entity, boolean.class);
			    	logger.info("::audienceMatched::"+ audienceMatched);
					//END
				}
			}
			
			//check for calendar restriction
			if(calendarId != null) {
				if(menuType != null && menuType.equalsIgnoreCase("menu"))
					rst3 = jdbcTemplateObject.queryForRowSet("select * from tbl_menu_scheduler where menu_id=? and calendar_id=?",
							new Object[]{id, calendarId});
				if(menuType != null && menuType.equalsIgnoreCase("subMenu"))
					rst3 = jdbcTemplateObject.queryForRowSet("select * from tbl_menu_scheduler where sub_menu_id=? and calendar_id=?",
							new Object[]{id, calendarId});
				while(rst3.next()) {
					timeRestriction = true;
					startDateTime = rst3.getString("start_datetime");
					endDateTime = rst3.getString("end_datetime");
					repeatType = rst3.getString("repeat_type");
					repeatWeeklyOn = rst3.getString("repeat_weekly_on");
					
					String dayNames[] = new DateFormatSymbols().getWeekdays();
					Calendar date2 = Calendar.getInstance();
					String dayName = dayNames[date2.get(Calendar.DAY_OF_WEEK)];
					logger.info("::Today is a::"+ dayName.substring(0, 2));
					
					if(repeatWeeklyOn.contains(dayName.substring(0, 2))) {
						logger.info("::day matched::");
						
						Date currentDate = new Date();
				        logger.info("::currentDate::" +currentDate);
				        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
				        String currentTime = sdf.format(currentDate);
				        logger.info("::currentTime::" +currentTime);
						if(isTimeBetweenTwoTimes(startDateTime, endDateTime, currentTime)) {
			                logger.info("::Given time lies between two times::");
			                timeMatched = true;
						}
			            else
			            	logger.info("::Given time doesn't lies between two times::");
					}
				}
			}
			
			if(positivePlaceRestriction && !positivePlaceMatched) {
				userMatched = false;
			} else if(negativePlaceRestriction && !negativePlaceMatched) {
				userMatched = false;
			} else if(audienceRestriction && !audienceMatched) {
				userMatched = false;
			} else if (timeRestriction && !timeMatched) {
				userMatched = false;
			}
		} catch(Exception e) {
			logger.info("::Exception in checkPlaceAndAudienceSegments::"+e.getLocalizedMessage());
			e.printStackTrace();
		}
		return userMatched;
	}
	
	public String geteMp2UserId(String appUserId) throws Exception {
		SqlRowSet rst = null;
		String emp2UserId = "";
		
		rst = jdbcTemplateObject.queryForRowSet("select emkit_user_id from tbl_app_user_detail where app_user_id=?",new Object[]{appUserId});
		if(rst.next()) {
			emp2UserId = rst.getString("emkit_user_id");
		}
		logger.info("::emp2UserId::" +emp2UserId);
		return emp2UserId;
	}
	
	public static boolean isTimeBetweenTwoTimes(String startTime, String endTime, String currentTime) throws ParseException {
        String reg = "^([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";
        if(startTime.matches(reg) && endTime.matches(reg) && currentTime.matches(reg)) {
            boolean valid  = false;
            //Start Time
            java.util.Date inTime = new SimpleDateFormat("HH:mm:ss").parse(startTime);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(inTime);
            
            //Current Time
            java.util.Date checkTime = new SimpleDateFormat("HH:mm:ss").parse(currentTime);
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(checkTime);
            
            //End Time
            java.util.Date finTime = new SimpleDateFormat("HH:mm:ss").parse(endTime);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(finTime);
            
            if(endTime.compareTo(startTime) < 0) {
                calendar2.add(Calendar.DATE, 1);
                calendar3.add(Calendar.DATE, 1);
            }
            logger.info("::startTime::" +calendar1.getTime());
            logger.info("::endTime::" +calendar2.getTime());
            logger.info("::currentTime::" +calendar3.getTime());
            
            java.util.Date actualTime = calendar3.getTime();
            if((actualTime.after(calendar1.getTime()) || actualTime.compareTo(calendar1.getTime())==0) && actualTime.before(calendar2.getTime())){
                valid = true;
            }
            return valid;
        } else {
             throw new IllegalArgumentException("not a valid time, expecting HH:MM:SS format");
        }
    }
}
