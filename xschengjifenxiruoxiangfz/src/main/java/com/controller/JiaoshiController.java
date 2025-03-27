
package com.controller;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import com.alibaba.fastjson.JSONObject;
import java.util.*;
import org.springframework.beans.BeanUtils;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.ContextLoader;
import javax.servlet.ServletContext;
import com.service.TokenService;
import com.utils.*;
import java.lang.reflect.InvocationTargetException;

import com.service.DictionaryService;
import org.apache.commons.lang3.StringUtils;
import com.annotation.IgnoreAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.entity.*;
import com.entity.view.*;
import com.service.*;
import com.utils.PageUtils;
import com.utils.R;
import com.alibaba.fastjson.*;

/**
 * 教师
 * 后端接口
 * @author
 * @email
*/
@RestController
@Controller
@RequestMapping("/jiaoshi")
public class JiaoshiController {
    private static final Logger logger = LoggerFactory.getLogger(JiaoshiController.class);

    private static final String TABLE_NAME = "jiaoshi";

    @Autowired
    private JiaoshiService jiaoshiService;


    @Autowired
    private TokenService tokenService;

    @Autowired
    private CaozuorizhiService caozuorizhiService;//操作日志
    @Autowired
    private DictionaryService dictionaryService;//字典
    @Autowired
    private ExampaperService exampaperService;//试卷表
    @Autowired
    private ExampapertopicService exampapertopicService;//试卷选题
    @Autowired
    private ExamquestionService examquestionService;//试题表
    @Autowired
    private ExamrecordService examrecordService;//考试记录表
    @Autowired
    private ExamredetailsService examredetailsService;//答题详情表
    @Autowired
    private ExamrewrongquestionService examrewrongquestionService;//错题表
    @Autowired
    private ForumService forumService;//论坛
    @Autowired
    private LiuyanService liuyanService;//留言板
    @Autowired
    private NewsService newsService;//公告信息
    @Autowired
    private YonghuService yonghuService;//学生
    @Autowired
    private UsersService usersService;//管理员


    /**
    * 后端列表
    */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("page方法:,,Controller:{},,params:{}",this.getClass().getName(),JSONObject.toJSONString(params));
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(false)
            return R.error(511,"永不会进入");
        else if("学生".equals(role))
            params.put("yonghuId",request.getSession().getAttribute("userId"));
        else if("教师".equals(role))
            params.put("jiaoshiId",request.getSession().getAttribute("userId"));
        CommonUtil.checkMap(params);
        PageUtils page = jiaoshiService.queryPage(params);

        //字典表数据转换
        List<JiaoshiView> list =(List<JiaoshiView>)page.getList();
        for(JiaoshiView c:list){
            //修改对应字典表字段
            dictionaryService.dictionaryConvert(c, request);
        }
        caozuorizhiService.insertCaozuorizhi(String.valueOf(request.getSession().getAttribute("role")),TABLE_NAME,String.valueOf(request.getSession().getAttribute("username")),"列表查询",list.toString());
        return R.ok().put("data", page);
    }

    /**
    * 后端详情
    */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id, HttpServletRequest request){
        logger.debug("info方法:,,Controller:{},,id:{}",this.getClass().getName(),id);
        JiaoshiEntity jiaoshi = jiaoshiService.selectById(id);
        if(jiaoshi !=null){
            //entity转view
            JiaoshiView view = new JiaoshiView();
            BeanUtils.copyProperties( jiaoshi , view );//把实体数据重构到view中
            //修改对应字典表字段
            dictionaryService.dictionaryConvert(view, request);
    caozuorizhiService.insertCaozuorizhi(String.valueOf(request.getSession().getAttribute("role")),TABLE_NAME,String.valueOf(request.getSession().getAttribute("username")),"单条数据查看",view.toString());
            return R.ok().put("data", view);
        }else {
            return R.error(511,"查不到数据");
        }

    }

    /**
    * 后端保存
    */
    @RequestMapping("/save")
    public R save(@RequestBody JiaoshiEntity jiaoshi, HttpServletRequest request){
        logger.debug("save方法:,,Controller:{},,jiaoshi:{}",this.getClass().getName(),jiaoshi.toString());

        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(false)
            return R.error(511,"永远不会进入");

        Wrapper<JiaoshiEntity> queryWrapper = new EntityWrapper<JiaoshiEntity>()
            .eq("username", jiaoshi.getUsername())
            .or()
            .eq("jiaoshi_phone", jiaoshi.getJiaoshiPhone())
            .or()
            .eq("jiaoshi_id_number", jiaoshi.getJiaoshiIdNumber())
            ;

        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        JiaoshiEntity jiaoshiEntity = jiaoshiService.selectOne(queryWrapper);
        if(jiaoshiEntity==null){
            jiaoshi.setCreateTime(new Date());
            jiaoshi.setPassword("123456");
            jiaoshiService.insert(jiaoshi);
            caozuorizhiService.insertCaozuorizhi(String.valueOf(request.getSession().getAttribute("role")),TABLE_NAME,String.valueOf(request.getSession().getAttribute("username")),"新增",jiaoshi.toString());
            return R.ok();
        }else {
            return R.error(511,"账户或者教师手机号或者教师身份证号已经被使用");
        }
    }

    /**
    * 后端修改
    */
    @RequestMapping("/update")
    public R update(@RequestBody JiaoshiEntity jiaoshi, HttpServletRequest request) throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        logger.debug("update方法:,,Controller:{},,jiaoshi:{}",this.getClass().getName(),jiaoshi.toString());
        JiaoshiEntity oldJiaoshiEntity = jiaoshiService.selectById(jiaoshi.getId());//查询原先数据

        String role = String.valueOf(request.getSession().getAttribute("role"));
//        if(false)
//            return R.error(511,"永远不会进入");
        if("".equals(jiaoshi.getJiaoshiPhoto()) || "null".equals(jiaoshi.getJiaoshiPhoto())){
                jiaoshi.setJiaoshiPhoto(null);
        }

            jiaoshiService.updateById(jiaoshi);//根据id更新
            List<String> strings = caozuorizhiService.clazzDiff(jiaoshi, oldJiaoshiEntity, request,new String[]{"updateTime"});
            caozuorizhiService.insertCaozuorizhi(String.valueOf(request.getSession().getAttribute("role")),TABLE_NAME,String.valueOf(request.getSession().getAttribute("username")),"修改",strings.toString());
            return R.ok();
    }



    /**
    * 删除
    */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids, HttpServletRequest request){
        logger.debug("delete:,,Controller:{},,ids:{}",this.getClass().getName(),ids.toString());
        List<JiaoshiEntity> oldJiaoshiList =jiaoshiService.selectBatchIds(Arrays.asList(ids));//要删除的数据
        jiaoshiService.deleteBatchIds(Arrays.asList(ids));

        caozuorizhiService.insertCaozuorizhi(String.valueOf(request.getSession().getAttribute("role")),TABLE_NAME,String.valueOf(request.getSession().getAttribute("username")),"删除",oldJiaoshiList.toString());
        return R.ok();
    }


    /**
     * 批量上传
     */
    @RequestMapping("/batchInsert")
    public R save( String fileName, HttpServletRequest request){
        logger.debug("batchInsert方法:,,Controller:{},,fileName:{}",this.getClass().getName(),fileName);
        Integer yonghuId = Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId")));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //.eq("time", new SimpleDateFormat("yyyy-MM-dd").format(new Date()))
        try {
            List<JiaoshiEntity> jiaoshiList = new ArrayList<>();//上传的东西
            Map<String, List<String>> seachFields= new HashMap<>();//要查询的字段
            Date date = new Date();
            int lastIndexOf = fileName.lastIndexOf(".");
            if(lastIndexOf == -1){
                return R.error(511,"该文件没有后缀");
            }else{
                String suffix = fileName.substring(lastIndexOf);
                if(!".xls".equals(suffix)){
                    return R.error(511,"只支持后缀为xls的excel文件");
                }else{
                    URL resource = this.getClass().getClassLoader().getResource("static/upload/" + fileName);//获取文件路径
                    File file = new File(resource.getFile());
                    if(!file.exists()){
                        return R.error(511,"找不到上传文件，请联系管理员");
                    }else{
                        List<List<String>> dataList = PoiUtil.poiImport(file.getPath());//读取xls文件
                        dataList.remove(0);//删除第一行，因为第一行是提示
                        for(List<String> data:dataList){
                            //循环
                            JiaoshiEntity jiaoshiEntity = new JiaoshiEntity();
//                            jiaoshiEntity.setUsername(data.get(0));                    //账户 要改的
//                            jiaoshiEntity.setPassword("123456");//密码
//                            jiaoshiEntity.setJiaoshiName(data.get(0));                    //教师姓名 要改的
//                            jiaoshiEntity.setJiaoshiPhone(data.get(0));                    //教师手机号 要改的
//                            jiaoshiEntity.setJiaoshiIdNumber(data.get(0));                    //教师身份证号 要改的
//                            jiaoshiEntity.setJiaoshiPhoto("");//详情和图片
//                            jiaoshiEntity.setSexTypes(Integer.valueOf(data.get(0)));   //性别 要改的
//                            jiaoshiEntity.setJiaoshiEmail(data.get(0));                    //电子邮箱 要改的
//                            jiaoshiEntity.setCreateTime(date);//时间
                            jiaoshiList.add(jiaoshiEntity);


                            //把要查询是否重复的字段放入map中
                                //账户
                                if(seachFields.containsKey("username")){
                                    List<String> username = seachFields.get("username");
                                    username.add(data.get(0));//要改的
                                }else{
                                    List<String> username = new ArrayList<>();
                                    username.add(data.get(0));//要改的
                                    seachFields.put("username",username);
                                }
                                //教师手机号
                                if(seachFields.containsKey("jiaoshiPhone")){
                                    List<String> jiaoshiPhone = seachFields.get("jiaoshiPhone");
                                    jiaoshiPhone.add(data.get(0));//要改的
                                }else{
                                    List<String> jiaoshiPhone = new ArrayList<>();
                                    jiaoshiPhone.add(data.get(0));//要改的
                                    seachFields.put("jiaoshiPhone",jiaoshiPhone);
                                }
                                //教师身份证号
                                if(seachFields.containsKey("jiaoshiIdNumber")){
                                    List<String> jiaoshiIdNumber = seachFields.get("jiaoshiIdNumber");
                                    jiaoshiIdNumber.add(data.get(0));//要改的
                                }else{
                                    List<String> jiaoshiIdNumber = new ArrayList<>();
                                    jiaoshiIdNumber.add(data.get(0));//要改的
                                    seachFields.put("jiaoshiIdNumber",jiaoshiIdNumber);
                                }
                        }

                        //查询是否重复
                         //账户
                        List<JiaoshiEntity> jiaoshiEntities_username = jiaoshiService.selectList(new EntityWrapper<JiaoshiEntity>().in("username", seachFields.get("username")));
                        if(jiaoshiEntities_username.size() >0 ){
                            ArrayList<String> repeatFields = new ArrayList<>();
                            for(JiaoshiEntity s:jiaoshiEntities_username){
                                repeatFields.add(s.getUsername());
                            }
                            return R.error(511,"数据库的该表中的 [账户] 字段已经存在 存在数据为:"+repeatFields.toString());
                        }
                         //教师手机号
                        List<JiaoshiEntity> jiaoshiEntities_jiaoshiPhone = jiaoshiService.selectList(new EntityWrapper<JiaoshiEntity>().in("jiaoshi_phone", seachFields.get("jiaoshiPhone")));
                        if(jiaoshiEntities_jiaoshiPhone.size() >0 ){
                            ArrayList<String> repeatFields = new ArrayList<>();
                            for(JiaoshiEntity s:jiaoshiEntities_jiaoshiPhone){
                                repeatFields.add(s.getJiaoshiPhone());
                            }
                            return R.error(511,"数据库的该表中的 [教师手机号] 字段已经存在 存在数据为:"+repeatFields.toString());
                        }
                         //教师身份证号
                        List<JiaoshiEntity> jiaoshiEntities_jiaoshiIdNumber = jiaoshiService.selectList(new EntityWrapper<JiaoshiEntity>().in("jiaoshi_id_number", seachFields.get("jiaoshiIdNumber")));
                        if(jiaoshiEntities_jiaoshiIdNumber.size() >0 ){
                            ArrayList<String> repeatFields = new ArrayList<>();
                            for(JiaoshiEntity s:jiaoshiEntities_jiaoshiIdNumber){
                                repeatFields.add(s.getJiaoshiIdNumber());
                            }
                            return R.error(511,"数据库的该表中的 [教师身份证号] 字段已经存在 存在数据为:"+repeatFields.toString());
                        }
                        jiaoshiService.insertBatch(jiaoshiList);
                        caozuorizhiService.insertCaozuorizhi(String.valueOf(request.getSession().getAttribute("role")),TABLE_NAME,String.valueOf(request.getSession().getAttribute("username")),"批量新增",jiaoshiList.toString());
                        return R.ok();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return R.error(511,"批量插入数据异常，请联系管理员");
        }
    }

    /**
    * 登录
    */
    @IgnoreAuth
    @RequestMapping(value = "/login")
    public R login(String username, String password, String captcha, HttpServletRequest request) {
        JiaoshiEntity jiaoshi = jiaoshiService.selectOne(new EntityWrapper<JiaoshiEntity>().eq("username", username));
        if(jiaoshi==null || !jiaoshi.getPassword().equals(password))
            return R.error("账号或密码不正确");
        String token = tokenService.generateToken(jiaoshi.getId(),username, "jiaoshi", "教师");
        R r = R.ok();
        r.put("token", token);
        r.put("role","教师");
        r.put("username",jiaoshi.getJiaoshiName());
        r.put("tableName","jiaoshi");
        r.put("userId",jiaoshi.getId());
        caozuorizhiService.insertCaozuorizhi(String.valueOf(request.getSession().getAttribute("role")),TABLE_NAME,String.valueOf(request.getSession().getAttribute("username")),"登录",jiaoshi.toString());
        return r;
    }

    /**
    * 注册
    */
    @IgnoreAuth
    @PostMapping(value = "/register")
    public R register(@RequestBody JiaoshiEntity jiaoshi, HttpServletRequest request) {
//    	ValidatorUtils.validateEntity(user);
        Wrapper<JiaoshiEntity> queryWrapper = new EntityWrapper<JiaoshiEntity>()
            .eq("username", jiaoshi.getUsername())
            .or()
            .eq("jiaoshi_phone", jiaoshi.getJiaoshiPhone())
            .or()
            .eq("jiaoshi_id_number", jiaoshi.getJiaoshiIdNumber())
            ;
        JiaoshiEntity jiaoshiEntity = jiaoshiService.selectOne(queryWrapper);
        if(jiaoshiEntity != null)
            return R.error("账户或者教师手机号或者教师身份证号已经被使用");
        jiaoshi.setCreateTime(new Date());
        jiaoshiService.insert(jiaoshi);

        caozuorizhiService.insertCaozuorizhi(String.valueOf(request.getSession().getAttribute("role")),TABLE_NAME,String.valueOf(request.getSession().getAttribute("username")),"注册",jiaoshi.toString());
        return R.ok();
    }

    /**
     * 重置密码
     */
    @GetMapping(value = "/resetPassword")
    public R resetPassword(Integer  id, HttpServletRequest request) {
        JiaoshiEntity jiaoshi = jiaoshiService.selectById(id);
        jiaoshi.setPassword("123456");
        jiaoshiService.updateById(jiaoshi);
            caozuorizhiService.insertCaozuorizhi(String.valueOf(request.getSession().getAttribute("role")),TABLE_NAME,String.valueOf(request.getSession().getAttribute("username")),"重置密码",jiaoshi.toString());
        return R.ok();
    }

	/**
	 * 修改密码
	 */
	@GetMapping(value = "/updatePassword")
	public R updatePassword(String  oldPassword, String  newPassword, HttpServletRequest request) {
        JiaoshiEntity jiaoshi = jiaoshiService.selectById((Integer)request.getSession().getAttribute("userId"));
		if(newPassword == null){
			return R.error("新密码不能为空") ;
		}
		if(!oldPassword.equals(jiaoshi.getPassword())){
			return R.error("原密码输入错误");
		}
		if(newPassword.equals(jiaoshi.getPassword())){
			return R.error("新密码不能和原密码一致") ;
		}
        jiaoshi.setPassword(newPassword);
		jiaoshiService.updateById(jiaoshi);
		return R.ok();
	}



    /**
     * 忘记密码
     */
    @IgnoreAuth
    @RequestMapping(value = "/resetPass")
    public R resetPass(String username, HttpServletRequest request) {
        JiaoshiEntity jiaoshi = jiaoshiService.selectOne(new EntityWrapper<JiaoshiEntity>().eq("username", username));
        if(jiaoshi!=null){
            jiaoshi.setPassword("123456");
            jiaoshiService.updateById(jiaoshi);
            caozuorizhiService.insertCaozuorizhi(String.valueOf(request.getSession().getAttribute("role")),TABLE_NAME,String.valueOf(request.getSession().getAttribute("username")),"忘记密码",jiaoshi.toString());
            return R.ok();
        }else{
           return R.error("账号不存在");
        }
    }


    /**
    * 获取用户的session用户信息
    */
    @RequestMapping("/session")
    public R getCurrJiaoshi(HttpServletRequest request){
        Integer id = (Integer)request.getSession().getAttribute("userId");
        JiaoshiEntity jiaoshi = jiaoshiService.selectById(id);
        if(jiaoshi !=null){
            //entity转view
            JiaoshiView view = new JiaoshiView();
            BeanUtils.copyProperties( jiaoshi , view );//把实体数据重构到view中

            //修改对应字典表字段
            dictionaryService.dictionaryConvert(view, request);
            return R.ok().put("data", view);
        }else {
            return R.error(511,"查不到数据");
        }
    }


    /**
    * 退出
    */
    @GetMapping(value = "logout")
    public R logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return R.ok("退出成功");
    }



    /**
    * 前端列表
    */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("list方法:,,Controller:{},,params:{}",this.getClass().getName(),JSONObject.toJSONString(params));

        CommonUtil.checkMap(params);
        PageUtils page = jiaoshiService.queryPage(params);

        //字典表数据转换
        List<JiaoshiView> list =(List<JiaoshiView>)page.getList();
        for(JiaoshiView c:list)
            dictionaryService.dictionaryConvert(c, request); //修改对应字典表字段

        caozuorizhiService.insertCaozuorizhi(String.valueOf(request.getSession().getAttribute("role")),TABLE_NAME,String.valueOf(request.getSession().getAttribute("username")),"列表查询",list.toString());
        return R.ok().put("data", page);
    }

    /**
    * 前端详情
    */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id, HttpServletRequest request){
        logger.debug("detail方法:,,Controller:{},,id:{}",this.getClass().getName(),id);
        JiaoshiEntity jiaoshi = jiaoshiService.selectById(id);
            if(jiaoshi !=null){


                //entity转view
                JiaoshiView view = new JiaoshiView();
                BeanUtils.copyProperties( jiaoshi , view );//把实体数据重构到view中

                //修改对应字典表字段
                dictionaryService.dictionaryConvert(view, request);
                    caozuorizhiService.insertCaozuorizhi(String.valueOf(request.getSession().getAttribute("role")),TABLE_NAME,String.valueOf(request.getSession().getAttribute("username")),"单条数据查看",view.toString());
                return R.ok().put("data", view);
            }else {
                return R.error(511,"查不到数据");
            }
    }


    /**
    * 前端保存
    */
    @RequestMapping("/add")
    public R add(@RequestBody JiaoshiEntity jiaoshi, HttpServletRequest request){
        logger.debug("add方法:,,Controller:{},,jiaoshi:{}",this.getClass().getName(),jiaoshi.toString());
        Wrapper<JiaoshiEntity> queryWrapper = new EntityWrapper<JiaoshiEntity>()
            .eq("username", jiaoshi.getUsername())
            .or()
            .eq("jiaoshi_phone", jiaoshi.getJiaoshiPhone())
            .or()
            .eq("jiaoshi_id_number", jiaoshi.getJiaoshiIdNumber())
//            .notIn("jiaoshi_types", new Integer[]{102})
            ;
        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        JiaoshiEntity jiaoshiEntity = jiaoshiService.selectOne(queryWrapper);
        if(jiaoshiEntity==null){
            jiaoshi.setCreateTime(new Date());
            jiaoshi.setPassword("123456");
        jiaoshiService.insert(jiaoshi);

            caozuorizhiService.insertCaozuorizhi(String.valueOf(request.getSession().getAttribute("role")),TABLE_NAME,String.valueOf(request.getSession().getAttribute("username")),"前台新增",jiaoshi.toString());
            return R.ok();
        }else {
            return R.error(511,"账户或者教师手机号或者教师身份证号已经被使用");
        }
    }

}

