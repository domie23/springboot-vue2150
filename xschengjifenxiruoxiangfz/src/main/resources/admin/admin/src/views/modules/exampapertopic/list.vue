<template>
	<div class="main-content">

		<!-- 条件查询 -->
		<div v-if="showFlag">
			<el-form :inline="true" :model="searchForm" class="form-content">
				<el-row :gutter="20" class="slt"
					:style="{justifyContent:contents.searchBoxPosition=='1'?'flex-start':contents.searchBoxPosition=='2'?'center':'flex-end'}">
					<el-form-item>
						<el-input prefix-icon="el-icon-search" v-model="searchForm.exampaperName" placeholder="试卷名称"
							clearable></el-input>
					</el-form-item>

					<el-form-item :label="contents.inputTitle == 1 ? '试题名称' : ''">
						<el-input prefix-icon="el-icon-search" v-model="searchForm.examquestionName" placeholder="试题名称"
							clearable></el-input>
					</el-form-item>

					<el-form-item>
						<el-button type="success" @click="search()">查询<i class="el-icon-search el-icon--right" />
						</el-button>
					</el-form-item>
				</el-row>
				<el-row class="ad"
					:style="{justifyContent:contents.btnAdAllBoxPosition=='1'?'flex-start':contents.btnAdAllBoxPosition=='2'?'center':'flex-end'}">
					<label  :span="20" v-if="zujuanTypes==1" style="margin-bottom: 10px;width: 80%;">
						<div style="margin-bottom: 10px;">
							<el-input style="width: 320px" type="number" min="0" :max="danXisting" placeholder="数量"
								v-model="danNum">
								<template slot="prepend">单选题数量:</template>
								<template slot="append">现:<span style="font-size: 25px;color: var(--publicMainColor)">{{danXisting}}</span>题；分数:</template>
							</el-input>
							<el-input style="width: 80px" placeholder="分数" type="number" min="0" max="100" v-model="danFen">
							</el-input>
							<el-input style="width: 320px;margin-left: 10px;" type="number" min="0" :max="duoXisting" placeholder="数量"
								v-model="duoNum">
								<template slot="prepend">多选题数量:</template>
								<template slot="append">现:<span style="font-size: 25px;color: var(--publicMainColor)">{{duoXisting}}</span>题；分数:</template>
							</el-input>
							<el-input style="width: 80px" placeholder="分数" type="number" min="0" max="100" v-model="duoFen">
							</el-input>
						</div>
						<div>
							<el-input style="width: 320px;" type="number" min="0" :max="panXisting" placeholder="数量"
								v-model="panNum">
								<template slot="prepend">判选题数量:</template>
								<template slot="append">现:<span style="font-size: 25px;color: var(--publicMainColor)">{{panXisting}}</span>题；分数:</template>
							</el-input>
							<el-input style="width: 80px" placeholder="分数" type="number" min="0" max="100" v-model="panFen">
							</el-input>
							<el-input style="width: 320px;margin-left: 10px;" type="number" min="0" :max="tianXisting" placeholder="数量"
								v-model="tianNum">
								<template slot="prepend">填空题数量:</template>
								<template slot="append">现:<span style="font-size: 25px;color: var(--publicMainColor)">{{tianXisting}}</span>题；分数:</template>
							</el-input>
							<el-input style="width: 80px" placeholder="分数" type="number" min="0" max="100" v-model="tianFen">
							</el-input>
						</div>
					</label>
					<el-col :span="zujuanTypes==1?6:24" style="margin-bottom: 10px;">
						<el-button type="success" icon="el-icon-plus" @click="addshiti()">
							{{zujuanTypes==1?"开始组卷":"添加考题"}}
						</el-button>
						<el-tag style="float: right;" size="small">试卷总分:<span style="font-size: 20px;">{{exampapertopicNumber}}</span></el-tag>
					</el-col>
				</el-row>
			</el-form>
			<div class="table-content">
				<el-table class="tables" :size="contents.tableSize" :show-header="contents.tableShowHeader"
					:header-row-style="headerRowStyle" :header-cell-style="headerCellStyle"
					:border="contents.tableBorder" :fit="contents.tableFit" :stripe="contents.tableStripe"
					:row-style="rowStyle" :cell-style="cellStyle"
					:style="{width: '100%',fontSize:contents.tableContentFontSize,color:contents.tableContentFontColor}"
					:data="dataList" v-loading="dataListLoading" @selection-change="selectionChangeHandler">
					<el-table-column label="索引" v-if="contents.tableIndex" type="index" width="50" />
					<el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign"
						prop="exampaperName" header-align="center" label="试卷名称">
						<template slot-scope="scope">
                           <span style="display: inline-block;white-space: nowrap; width: 80%;overflow: hidden;text-overflow:ellipsis;">
								{{scope.row.exampaperName}}
							</span>
						</template>
					</el-table-column>
					<el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign"
						prop="examquestionName" header-align="center" label="试题名称">
						<template slot-scope="scope">
                           	<span style="display: inline-block;white-space: nowrap; width: 80%;overflow: hidden;text-overflow:ellipsis;">
								{{scope.row.examquestionName}}
							</span>
						</template>
					</el-table-column>
					<el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign"
						prop="examquestionTypes" header-align="center" label="试题类型">
						<template slot-scope="scope">
							{{scope.row.examquestionValue}}
						</template>
					</el-table-column>
					<el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign"
						prop="exampapertopicNumber" header-align="center" label="试题分数">
						<template slot-scope="scope">
							{{scope.row.exampapertopicNumber}}
						</template>
					</el-table-column>
					<el-table-column width="300" :align="contents.tableAlign" header-align="center" label="操作">
						<template slot-scope="scope">
							<el-button v-if="isAuth('exampaper','查看')" type="success" icon="el-icon-tickets" size="mini"
								@click="addOrUpdateHandler(scope.row.id,'info')">详情</el-button>
							<el-button v-if="isAuth('exampaper','添加考题')" type="success" icon="el-icon-tickets"
								size="mini" @click="addOrUpdateHandler(scope.row.id)">修改分数</el-button>

							<el-button v-if="isAuth('exampaper','删除')" type="danger" icon="el-icon-delete" size="mini"
								@click="deleteHandler(scope.row.id)">删除</el-button>

						</template>
					</el-table-column>
				</el-table>
				<el-pagination clsss="pages" :layout="layouts" @size-change="sizeChangeHandle"
					@current-change="currentChangeHandle" :current-page="pageIndex" :page-sizes="[10, 20, 50, 100]"
					:page-size="Number(contents.pageEachNum)" :total="totalPage" :small="contents.pageStyle"
					class="pagination-content" :background="contents.pageBtnBG"
					:style="{textAlign:contents.pagePosition==1?'left':contents.pagePosition==2?'center':'right'}">
				</el-pagination>
			</div>
		</div>
		<!-- 添加/修改页面  将父组件的search方法传递给子组件-->
		<add-or-update v-if="addOrUpdateFlag" :parent="this" ref="addOrUpdate"></add-or-update>

		<!-- 统计报表 -->
		<el-dialog title="统计报表" :visible.sync="chartVisiable" width="auto">
			<el-date-picker v-model="echartsDate" type="year" placeholder="选择年">
			</el-date-picker>
			<el-button @click="chartDialog()">查询</el-button>
			<div id="statistic" style="width:100%;height:600px;"></div>
			<span slot="footer" class="dialog-footer">
				<el-button @click="chartVisiable = false">关闭</el-button>
			</span>
		</el-dialog>

		<!-- 添加试题 -->
		<el-dialog title="添加试题" :visible.sync="questionsTableVisible" width="80%">
			<el-form :inline="true" :model="questionsSearchForm" class="form-content">
				<el-row :gutter="20" class="slt"
					:style="{justifyContent:contents.searchBoxPosition=='1'?'flex-start':contents.searchBoxPosition=='2'?'center':'flex-end'}">
					<el-form-item :label="contents.inputTitle == 1 ? '试题名称' : ''">
						<el-input prefix-icon="el-icon-search" v-model="questionsSearchForm.examquestionName"
							placeholder="试题名称" clearable></el-input>
					</el-form-item>
					<el-form-item :label="contents.inputTitle == 1 ? '试题类型' : ''">
						<el-select v-model="questionsSearchForm.examquestionTypes" placeholder="请选择试题类型">
							<el-option label="=-请选择-=" value=""></el-option>
							<el-option v-for="(item,index) in examquestionTypesSelectSearch" v-bind:key="index"
								:label="item.indexName" :value="item.codeIndex">
							</el-option>
						</el-select>
					</el-form-item>
					<el-form-item>
						<el-button type="success" @click="questionsSearch()">查询<i
								class="el-icon-search el-icon--right" /></el-button>
					</el-form-item>
				</el-row>
			</el-form>
			<el-table class="tables" :size="contents.tableSize" :show-header="contents.tableShowHeader"
				:header-row-style="headerRowStyle" :header-cell-style="headerCellStyle" :border="contents.tableBorder"
				:fit="contents.tableFit" :stripe="contents.tableStripe" :row-style="rowStyle" :cell-style="cellStyle"
				:style="{width: '100%',fontSize:contents.tableContentFontSize,color:contents.tableContentFontColor}"
				:data="questionsList">
				<el-table-column label="索引" v-if="contents.tableIndex" type="index" width="50" />
				<el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign" prop="examquestionName"
					header-align="center" label="试题名称">
					<template slot-scope="scope">
					   <span style="display: inline-block;white-space: nowrap; width: 80%;overflow: hidden;text-overflow:ellipsis;">
							{{scope.row.examquestionName}}
						</span>
					</template>
				</el-table-column>
		
				<el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign"
					prop="examquestionAnswer" header-align="center" label="正确答案">
					<template slot-scope="scope">
					   <span style="display: inline-block;white-space: nowrap; width: 80%;overflow: hidden;text-overflow:ellipsis;">
							{{scope.row.examquestionAnswer}}
						</span>
					</template>
				</el-table-column>
		
				<el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign"
					prop="examquestionAnalysis" header-align="center" label="答案解析">
					<template slot-scope="scope">
					   <span style="display: inline-block;white-space: nowrap; width: 80%;overflow: hidden;text-overflow:ellipsis;">
							{{scope.row.examquestionAnalysis}}
						</span>
					</template>
				</el-table-column>
				<el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign"
					prop="examquestionTypes" header-align="center" label="试题类型">
					<template slot-scope="scope">
						{{scope.row.examquestionValue}}
					</template>
				</el-table-column>
				<el-table-column width="150" :align="contents.tableAlign" header-align="center" label="操作">
					<template slot-scope="scope">
						<el-button type="success" icon="el-icon-tickets" size="mini"
							@click="addQuestions(scope.row.id,'info')">添加</el-button>
					</template>
				</el-table-column>
			</el-table>
			<el-pagination clsss="pages" :layout="layouts" @size-change="questionsSizeChangeHandle"
				@current-change="questionsCurrentChangeHandle" :current-page="questionsPageIndex"
				:page-sizes="[10, 20, 50]" :page-size="Number(contents.pageEachNum)" :total="questionsPageTotalPage"
				:small="contents.pageStyle" class="pagination-content" :background="contents.pageBtnBG"
				:style="{textAlign:contents.pagePosition==1?'left':contents.pagePosition==2?'center':'right'}">
			</el-pagination>
		</el-dialog>

	</div>
</template>
<script>
	import AddOrUpdate from "./add-or-update.vue";
	import styleJs from "../../../utils/style.js";
	import utilsJs, {getYearFormat,getMonthFormat,getDateFormat,getDatetimeFormat} from "../../../utils/utils.js";

	export default {
		data() {
			return {
				//试卷id
				exampaperId: null,
				//组卷类型
				zujuanTypes:null,
				// manualOrAutomatic: false, //手动自动组卷
				//自动选题设置题目数量 题目分数
				danNum: 0, //单选数量
				danFen: 0, //单选分数
				danXisting: 0, //现有数量
				duoNum: 0, //多选数量
				duoFen: 0, //多选分数
				duoXisting: 0, //现有数量
				panNum: 0, //判断数量
				panFen: 0, //判断分数
				panXisting: 0, //现有数量
				tianNum: 0, //填空数量
				tianFen: 0, //填空分数
				tianXisting: 0, //现有数量
				
				questionsTableVisible: false, //手动添加试题弹框
				questionsList: [], //试题数据集合
				//试题数据集合分页信息
				questionsPageIndex: 1,
				questionsPageSize: 10,
				questionsPageTotalPage: 0,
				//试题数据集合条件查询
				questionsSearchForm: {
					key: ""
				},
				//试题类型数据
				examquestionTypesSelectSearch: [],
				//总分数
				exampapertopicNumber: 0,

				searchForm: {
					key: ""
				},
				sessionTable: "", //登录账户所在表名
				role: "", //权限
				userId: "", //当前登录人的id
				//级联表下拉框搜索条件
				exampaperTypesSelectSearch: [],
				kemuTypesSelectSearch: [],
				//当前表下拉框搜索条件
				form: {
					id: null,
					exampaperId: null,
					examquestionId: null,
					exampapertopicNumber: null,
					createTime: null,
				},
				dataList: [],
				pageIndex: 1,
				pageSize: 10,
				totalPage: 0,
				dataListLoading: false,
				dataListSelections: [],
				showFlag: true,
				sfshVisiable: false,
				shForm: {},
				chartVisiable: false,
				echartsDate: new Date(), //echarts的时间查询字段
				addOrUpdateFlag: false,
				contents: null,
				layouts: '',
				//导出excel
				json_fields: {
					//级联表字段
					'试卷名称': 'exampaperName',
					'考试时长(分钟)': 'exampaperDate',
					'试卷总分数': 'exampaperMyscore',
					'科目': 'kemuTypes',
					'单选题数量': 'danNum',
					'单选题每题分数': 'danFen',
					'多选题数量': 'duoNum',
					'多选题每题分数': 'duoFen',
					'判断题数量': 'panNum',
					'判断题每题分数': 'panFen',
					'填空题数量': 'tianNum',
					'填空题每题分数': 'tianFen',
					'试卷状态': 'exampaperTypes',
					'试题名称': 'examquestionName',
					'科目': 'kemuTypes',
					'选项，json字符串': 'examquestionOptions',
					'正确答案': 'examquestionAnswer',
					'答案解析': 'examquestionAnalysis',
					'试题类型': 'examquestionTypes',
					//本表字段
					'试题分数': "exampapertopicNumber",
				},

			};
		},
		created() {
			this.contents = styleJs.listStyle();
			this.init();
			this.getDataList();
			this.contentStyleChange()
		},
		mounted() {
			//获取当前登录学生的信息
			this.sessionTable = this.$storage.get("sessionTable");
			this.role = this.$storage.get("role");
			this.userId = this.$storage.get("userId");

			if (this.$route.query.exampaperId != null && this.$route.query.zujuanTypes != null) {
				this.exampaperId = this.$route.query.exampaperId
				this.zujuanTypes = this.$route.query.zujuanTypes
			}
			
			//查询现有全部试题
			this.selectQuestions()

			//填充下拉框选项
			this.$http({
				url: "dictionary/page?dicCode=examquestion_types&page=1&limit=100",
				method: "get",
			}).then(({
				data
			}) => {
				if (data && data.code === 0) {
					this.examquestionTypesSelectSearch = data.data.list;
				}
			});

		},
		filters: {
			htmlfilter: function(val) {
				return val.replace(/<[^>]*>/g).replace(/undefined/g, '');
			}
		},
		components: {
			AddOrUpdate,
		},
		computed: {
			getActionUrl: function() {
				return `/` + this.$base.name + `/file/upload`;
			}
		},
		methods: {
			chartDialog() {
				let _this = this;
				let params = {
					dateFormat: "%Y", //%Y-%m
					riqi: _this.echartsDate.getFullYear(),
					// riqi :_this.echartsDate.getFullYear()+"-"+(_this.echartsDate.getMonth() + 1 < 10 ? '0' + (_this.echartsDate.getMonth() + 1) : _this.echartsDate.getMonth() + 1),
					thisTable: { //当前表
						tableName: 'exampapertopic', //当前表表名,
						sumColum: 'exampapertopic_number', //求和字段
						date: 'insert_time', //分组日期字段
						// string : 'exampapertopic_name',//分组字符串字段
						// types : 'exampapertopic_types',//分组下拉框字段
					},
					// joinTable : {//级联表（可以不存在）
					//     tableName :'yonghu',//级联表表名
					//     // date : 'insert_time',//分组日期字段
					//     string : 'yonghu_name',//分组字符串字段
					//     // types : 'yonghu_types',//分组下拉框字段
					// }
				}
				_this.chartVisiable = true;
				_this.$nextTick(() => {
					var statistic = this.$echarts.init(document.getElementById("statistic"), 'macarons');
					this.$http({
						url: "barSum",
						method: "get",
						params: params
					}).then(({
						data
					}) => {
						if (data && data.code === 0) {

							//柱状图 求和 已成功使用
							//start
							let yAxisName = "数值"; //根据查询数据具体改(单列要改,多列不改)
							let xAxisName = "月份";
							let series = []; //具体数据值
							data.data.yAxis.forEach(function(item, index) {
								let tempMap = {};
								// tempMap.name= ["数值"];//根据查询数据具体改(单列要改,多列不改)
								tempMap.name = data.data.legend[index];
								tempMap.type = 'bar';
								tempMap.data = item;
								series.push(tempMap);

							})

							var option = {
								tooltip: {
									trigger: 'axis',
									axisPointer: {
										type: 'cross',
										crossStyle: {
											color: '#999'
										}
									}
								},
								toolbox: {
									feature: {
										// dataView: { show: true, readOnly: false },  // 数据查看
										magicType: {
											show: true,
											type: ['line', 'bar']
										}, //切换图形展示方式
										// restore: { show: true }, // 刷新
										saveAsImage: {
											show: true
										} //保存
									}
								},
								legend: {
									data: data.data.legend //标题  可以点击导致某一列数据消失
								},
								xAxis: [{
									type: 'category',
									axisLabel: {
										interval: 0
									},
									name: xAxisName,
									data: data.data.xAxis,
									axisPointer: {
										type: 'shadow'
									}
								}],
								yAxis: [{
									type: 'value', //不能改
									name: yAxisName, //y轴单位
									axisLabel: {
										formatter: '{value}' // 后缀
									}
								}],
								series: series //具体数据
							};
							// 使用刚指定的配置项和数据显示图表。
							statistic.setOption(option, true);
							//根据窗口的大小变动图表
							window.onresize = function() {
								statistic.resize();
							};
							//end
						} else {
							this.$message({
								message: "报表未查询到数据",
								type: "success",
								duration: 1500,
								onClose: () => {
									this.search();
								}
							});
						}
					});
				});
				////饼状图
				//_this.chartVisiable = true;
				// this.$nextTick(()=>{
				//     var statistic = this.$echarts.init(document.getElementById("statistic"),'macarons');
				//     let params = {
				//         tableName: "exampapertopic",
				//         groupColumn: "exampapertopic_types",
				//     }
				//     this.$http({
				//         url: "newSelectGroupCount",
				//         method: "get",
				//         params: params
				//     }).then(({data}) => {
				//         if (data && data.code === 0) {
				//             let res = data.data;
				//             let xAxis = [];
				//             let yAxis = [];
				//             let pArray = []
				//             var option = {};
				//             for(let i=0;i<res.length;i++){
				//                 xAxis.push(res[i].name);
				//                 yAxis.push(res[i].value);
				//                 pArray.push({
				//                     value: res[i].value,
				//                     name: res[i].name
				//                 })
				//                 option = {
				//                     title: {
				//                         text: '保险合同类型统计',
				//                         left: 'center'
				//                     },
				//                     tooltip: {
				//                         trigger: 'item',
				//                         formatter: '{b} : {c} ({d}%)'
				//                     },
				//                     legend: {
				//                         orient: 'vertical',
				//                         left: 'left'
				//                     },
				//                     series: [
				//                         {
				//                             type: 'pie',
				//                             radius: '55%',
				//                             center: ['50%', '60%'],
				//                             data: pArray,
				//                             emphasis: {
				//                                 itemStyle: {
				//                                     shadowBlur: 10,
				//                                     shadowOffsetX: 0,
				//                                     shadowColor: 'rgba(0, 0, 0, 0.5)'
				//                                 }
				//                             }
				//                         }
				//                     ]
				//                 };
				//             }
				//                 statistic.setOption(option,true);
				//                 window.onresize = function() {
				//                     statistic.resize();
				//                 };
				//         }
				//     });
				// })
			},
			contentStyleChange() {
				this.contentSearchStyleChange()
				this.contentBtnAdAllStyleChange()
				this.contentSearchBtnStyleChange()
				this.contentTableBtnStyleChange()
				this.contentPageStyleChange()
			},
			contentSearchStyleChange() {
				this.$nextTick(() => {
					document.querySelectorAll('.form-content .slt .el-input__inner').forEach(el => {
						let textAlign = 'left'
						if (this.contents.inputFontPosition == 2)
							textAlign = 'center'
						if (this.contents.inputFontPosition == 3) textAlign = 'right'
						el.style.textAlign = textAlign
						el.style.height = this.contents.inputHeight
						el.style.lineHeight = this.contents.inputHeight
						el.style.color = this.contents.inputFontColor
						el.style.fontSize = this.contents.inputFontSize
						el.style.borderWidth = this.contents.inputBorderWidth
						el.style.borderStyle = this.contents.inputBorderStyle
						el.style.borderColor = this.contents.inputBorderColor
						el.style.borderRadius = this.contents.inputBorderRadius
						el.style.backgroundColor = this.contents.inputBgColor
					})
					if (this.contents.inputTitle) {
						document.querySelectorAll('.form-content .slt .el-form-item__label').forEach(el => {
							el.style.color = this.contents.inputTitleColor
							el.style.fontSize = this.contents.inputTitleSize
							el.style.lineHeight = this.contents.inputHeight
						})
					}
					setTimeout(() => {
						document.querySelectorAll('.form-content .slt .el-input__prefix').forEach(el => {
							el.style.color = this.contents.inputIconColor
							el.style.lineHeight = this.contents.inputHeight
						})
						document.querySelectorAll('.form-content .slt .el-input__suffix').forEach(el => {
							el.style.color = this.contents.inputIconColor
							el.style.lineHeight = this.contents.inputHeight
						})
						document.querySelectorAll('.form-content .slt .el-input__icon').forEach(el => {
							el.style.lineHeight = this.contents.inputHeight
						})
					}, 10)
				})
			},
			// 搜索按钮
			contentSearchBtnStyleChange() {
				this.$nextTick(() => {
					document.querySelectorAll('.form-content .slt .el-button--success').forEach(el => {
						el.style.height = this.contents.searchBtnHeight
						el.style.color = this.contents.searchBtnFontColor
						el.style.fontSize = this.contents.searchBtnFontSize
						el.style.borderWidth = this.contents.searchBtnBorderWidth
						el.style.borderStyle = this.contents.searchBtnBorderStyle
						el.style.borderColor = this.contents.searchBtnBorderColor
						el.style.borderRadius = this.contents.searchBtnBorderRadius
						el.style.backgroundColor = this.contents.searchBtnBgColor
					})
				})

				
			},
			// 新增、批量删除
			contentBtnAdAllStyleChange() {
				this.$nextTick(() => {
					document.querySelectorAll('.form-content .ad .el-button--success').forEach(el => {
						el.style.height = this.contents.btnAdAllHeight
						el.style.color = this.contents.btnAdAllAddFontColor
						el.style.fontSize = this.contents.btnAdAllFontSize
						el.style.borderWidth = this.contents.btnAdAllBorderWidth
						el.style.borderStyle = this.contents.btnAdAllBorderStyle
						el.style.borderColor = this.contents.btnAdAllBorderColor
						el.style.borderRadius = this.contents.btnAdAllBorderRadius
						el.style.backgroundColor = this.contents.btnAdAllAddBgColor
					})
					document.querySelectorAll('.form-content .ad .el-button--danger').forEach(el => {
						el.style.height = this.contents.btnAdAllHeight
						el.style.color = this.contents.btnAdAllDelFontColor
						el.style.fontSize = this.contents.btnAdAllFontSize
						el.style.borderWidth = this.contents.btnAdAllBorderWidth
						el.style.borderStyle = this.contents.btnAdAllBorderStyle
						el.style.borderColor = this.contents.btnAdAllBorderColor
						el.style.borderRadius = this.contents.btnAdAllBorderRadius
						el.style.backgroundColor = this.contents.btnAdAllDelBgColor
					})
					document.querySelectorAll('.form-content .ad .el-button--warning').forEach(el => {
						el.style.height = this.contents.btnAdAllHeight
						el.style.color = this.contents.btnAdAllWarnFontColor
						el.style.fontSize = this.contents.btnAdAllFontSize
						el.style.borderWidth = this.contents.btnAdAllBorderWidth
						el.style.borderStyle = this.contents.btnAdAllBorderStyle
						el.style.borderColor = this.contents.btnAdAllBorderColor
						el.style.borderRadius = this.contents.btnAdAllBorderRadius
						el.style.backgroundColor = this.contents.btnAdAllWarnBgColor
					})
				})
			},
			// 表格
			rowStyle({
				row,
				rowIndex
			}) {
				if (rowIndex % 2 == 1) {
					if (this.contents.tableStripe) {
						return {
							color: this.contents.tableStripeFontColor
						}
					}
				} else {
					return ''
				}
			},
			cellStyle({
				row,
				rowIndex
			}) {
				if (rowIndex % 2 == 1) {
					if (this.contents.tableStripe) {
						return {
							backgroundColor: this.contents.tableStripeBgColor
						}
					}
				} else {
					return ''
				}
			},
			headerRowStyle({
				row,
				rowIndex
			}) {
				return {
					color: this.contents.tableHeaderFontColor
				}
			},
			headerCellStyle({
				row,
				rowIndex
			}) {
				return {
					backgroundColor: this.contents.tableHeaderBgColor
				}
			},
			// 表格按钮
			contentTableBtnStyleChange() {
				// this.$nextTick(()=>{
				//   setTimeout(()=>{
				//     document.querySelectorAll('.table-content .tables .el-table__body .el-button--success').forEach(el=>{
				//       el.style.height = this.contents.tableBtnHeight
				//       el.style.color = this.contents.tableBtnDetailFontColor
				//       el.style.fontSize = this.contents.tableBtnFontSize
				//       el.style.borderWidth = this.contents.tableBtnBorderWidth
				//       el.style.borderStyle = this.contents.tableBtnBorderStyle
				//       el.style.borderColor = this.contents.tableBtnBorderColor
				//       el.style.borderRadius = this.contents.tableBtnBorderRadius
				//       el.style.backgroundColor = this.contents.tableBtnDetailBgColor
				//     })
				//     document.querySelectorAll('.table-content .tables .el-table__body .el-button--primary').forEach(el=>{
				//       el.style.height = this.contents.tableBtnHeight
				//       el.style.color = this.contents.tableBtnEditFontColor
				//       el.style.fontSize = this.contents.tableBtnFontSize
				//       el.style.borderWidth = this.contents.tableBtnBorderWidth
				//       el.style.borderStyle = this.contents.tableBtnBorderStyle
				//       el.style.borderColor = this.contents.tableBtnBorderColor
				//       el.style.borderRadius = this.contents.tableBtnBorderRadius
				//       el.style.backgroundColor = this.contents.tableBtnEditBgColor
				//     })
				//     document.querySelectorAll('.table-content .tables .el-table__body .el-button--danger').forEach(el=>{
				//       el.style.height = this.contents.tableBtnHeight
				//       el.style.color = this.contents.tableBtnDelFontColor
				//       el.style.fontSize = this.contents.tableBtnFontSize
				//       el.style.borderWidth = this.contents.tableBtnBorderWidth
				//       el.style.borderStyle = this.contents.tableBtnBorderStyle
				//       el.style.borderColor = this.contents.tableBtnBorderColor
				//       el.style.borderRadius = this.contents.tableBtnBorderRadius
				//       el.style.backgroundColor = this.contents.tableBtnDelBgColor
				//     })

				//   }, 50)
				// })
			},
			// 分页
			contentPageStyleChange() {
				let arr = []
				if (this.contents.pageTotal) arr.push('total')
				if (this.contents.pageSizes) arr.push('sizes')
				if (this.contents.pagePrevNext) {
					arr.push('prev')
					if (this.contents.pagePager) arr.push('pager')
					arr.push('next')
				}
				if (this.contents.pageJumper) arr.push('jumper')
				this.layouts = arr.join()
				this.contents.pageEachNum = 10
			},

			init() {},
			search() {
				this.pageIndex = 1;
				this.getDataList();
			},
			// 获取数据列表
			getDataList() {
				let _this = this
				_this.dataListLoading = true;
				let params = {
					page: _this.pageIndex,
					limit: _this.pageSize,
				}
					
				if(this.$route.query.exampaperId !=null){
					params['exampaperId'] = this.$route.query.exampaperId
				}

				if (_this.searchForm.exampaperName != '' && _this.searchForm.exampaperName != undefined) {
					params['exampaperName'] = '%' + _this.searchForm.exampaperName + '%'
				}

				if (_this.searchForm.kemuTypes != '' && _this.searchForm.kemuTypes != undefined) {
					params['kemuTypes'] = _this.searchForm.kemuTypes
				}

				if (_this.searchForm.exampaperTypes != '' && _this.searchForm.exampaperTypes != undefined) {
					params['exampaperTypes'] = _this.searchForm.exampaperTypes
				}

				if (_this.searchForm.examquestionName != '' && _this.searchForm.examquestionName != undefined) {
					params['examquestionName'] = '%' + _this.searchForm.examquestionName + '%'
				}

				if (_this.searchForm.kemuTypes != '' && _this.searchForm.kemuTypes != undefined) {
					params['kemuTypes'] = _this.searchForm.kemuTypes
				}

				_this.$http({
					url: "exampapertopic/page",
					method: "get",
					params: params
				}).then(({
					data
				}) => {
					if (data && data.code === 0) {
						_this.dataList = data.data.list;
						_this.totalPage = data.data.total;
						//查询并设置总分数
						_this.$http({
							url: "exampapertopic/sumExampapertopicNumber",
							method: "get",
							params: {exampaperId:this.$route.query.exampaperId}
						}).then(({data}) => {
							_this.exampapertopicNumber = data.exampapertopicNumber
						});
					} else {
						_this.dataList = [];
						_this.totalPage = 0;
					}
					_this.dataListLoading = false;
				});

				//查询级联表搜索条件所有列表
				this.$http({
					url: "dictionary/page?dicCode=kemu_types&page=1&limit=100",
					method: "get",
					page: 1,
					limit: 100,
				}).then(({
					data
				}) => {
					if (data && data.code === 0) {
						this.kemuTypesSelectSearch = data.data.list;
					}
				});
				this.$http({
					url: "dictionary/page?dicCode=exampaper_types&page=1&limit=100",
					method: "get",
					page: 1,
					limit: 100,
				}).then(({
					data
				}) => {
					if (data && data.code === 0) {
						this.exampaperTypesSelectSearch = data.data.list;
					}
				});
				this.$http({
					url: "dictionary/page?dicCode=kemu_types&page=1&limit=100",
					method: "get",
					page: 1,
					limit: 100,
				}).then(({
					data
				}) => {
					if (data && data.code === 0) {
						this.kemuTypesSelectSearch = data.data.list;
					}
				});
				//查询当前表搜索条件所有列表
			},
			//每页数
			sizeChangeHandle(val) {
				this.pageSize = val;
				this.pageIndex = 1;
				this.getDataList();
			},
			// 当前页
			currentChangeHandle(val) {
				this.pageIndex = val;
				this.getDataList();
			},
			// 多选
			selectionChangeHandler(val) {
				this.dataListSelections = val;
			},
			// 添加/修改
			addOrUpdateHandler(id, type) {
				this.showFlag = false;
				this.addOrUpdateFlag = true;
				this.crossAddOrUpdateFlag = false;
				if (type != 'info') {
					type = 'else';
				}
				this.$nextTick(() => {
					this.$refs.addOrUpdate.init(id, type);
				});
			},
			// 下载
			download(file) {
				window.open(" ${file} ")
			},

			//选题每页数
			questionsSizeChangeHandle(val) {
				this.questionsPageSize = val;
				this.questionsPageIndex = 1;
				this.selectQuestions();
			},
			//选题当前页
			questionsCurrentChangeHandle(val) {
				this.questionsPageIndex = val;
				this.selectQuestions();
			},
			questionsSearch() {
				this.questionsPageIndex = 1;
				this.selectQuestions();
			},
			//判断 自动、手动组卷
			addshiti() {
				if (this.zujuanTypes==1) {
					if(this.dataList.length>0){
						return this.$message.error("请不要重复组卷！！！！(清空组卷信息后可再次重新组卷)")
					}
					//自动组卷
					if ((this.danNum == 0 || this.danNum == null || this.danFen == 0 || this.danFen == null)
					&&(this.duoNum == 0 || this.duoNum == null || this.duoFen == 0 || this.duoFen == null)
					&&(this.panNum == 0 || this.panNum == null || this.panFen == 0 || this.panFen == null)
					&&(this.tianNum == 0 || this.tianNum == null || this.tianFen == 0 || this.tianFen == null)
					) {
						return this.$message.error("题数量、题分数不可全部为空或为0")
					}
					
					let params = {
						kemuTypes: this.$route.query.kemuTypes,
						exampaperId: this.exampaperId,
						danNum:this.danNum,
						danFen:this.danFen,
						duoNum:this.duoNum,
						duoFen:this.duoFen,
						panNum:this.panNum,
						panFen:this.panFen,
						tianNum:this.tianNum,
						tianFen:this.tianFen,
					}
					this.$http({
						url: "exampapertopic/zidongzujuan",
						method: "get",
						params: params
					}).then(({
						data
					}) => {
						if (data && data.code === 0) {
							this.$message({
								message: "操作成功",
								type: "success",
								duration: 1500,
								onClose: () => {
									this.search();
									this.selectQuestions()
								}
							});
						} else {
							this.$message.error(data.msg);
						}
					});
					
				} else {
					this.selectQuestions()
					this.questionsTableVisible = true
					//手动组卷	
				}
			},
			selectQuestions(){
				let _this = this
				_this.danXisting = 0;_this.duoXisting = 0;_this.panXisting = 0;_this.tianXisting =0;
				let params = {}
				if(this.zujuanTypes==1){
					params['page'] = 1
					params['limit'] = 9999
				}else{ 
					params['page'] = _this.questionsPageIndex
					params['limit'] = _this.questionsPageSize
				}
				
				if (_this.$route.query.kemuTypes != null) {
					params['kemuTypes'] = _this.$route.query.kemuTypes
				}
				if (_this.questionsSearchForm.examquestionName != '' && _this.questionsSearchForm.examquestionName !=
					undefined) {
					params['examquestionName'] = '%' + _this.questionsSearchForm.examquestionName + '%'
				}
				
				if (_this.questionsSearchForm.kemuTypes != '' && _this.questionsSearchForm.kemuTypes != undefined) {
					params['kemuTypes'] = _this.questionsSearchForm.kemuTypes
				}
				
				if (_this.questionsSearchForm.examquestionTypes != '' && _this.questionsSearchForm.examquestionTypes !=
					undefined) {
					params['examquestionTypes'] = _this.questionsSearchForm.examquestionTypes
				}
				_this.$http({
					url: "examquestion/page",
					method: "get",
					params: params
				}).then(({
					data
				}) => {
					if (data && data.code === 0) {
						_this.questionsList = data.data.list;
						_this.questionsPageTotalPage = data.data.total;
						_this.questionsList.forEach(function(item, index){
							if(item.examquestionTypes==1){
								_this.danXisting = _this.danXisting+1
							}
							if(item.examquestionTypes==2){
								_this.duoXisting = _this.duoXisting+1
							}
							if(item.examquestionTypes==3){
								_this.panXisting = _this.panXisting+1
							}
							if(item.examquestionTypes==4){
								_this.tianXisting = _this.tianXisting+1
							}
						})
					} else {
						_this.questionsList = [];
						_this.questionsPageTotalPage = 0;
					}
				});
			},
			//手动组卷添加试题
			addQuestions(id) {
				let data = {
					exampaperId: this.exampaperId,
					examquestionId: id,
					exampapertopicNumber: 0
				}
				this.$http({
					url: "exampapertopic/save",
					method: "post",
					data: data
				}).then(({
					data
				}) => {
					if (data && data.code === 0) {
						this.$message({
							message: "添加试题成功",
							type: "success",
							duration: 1500,
							onClose: () => {
								this.search();
							}
						});
					} else {
						this.$message.error(data.msg);
					}
				});
			},


			// 删除
			deleteHandler(id) {
				var ids = id ? [Number(id)] : this.dataListSelections.map(item => {
					return Number(item.id);
				});

				this.$confirm(`确定进行[${id ? "删除" : "批量删除"}]操作?`, "提示", {
					confirmButtonText: "确定",
					cancelButtonText: "取消",
					type: "warning"
				}).then(() => {
					this.$http({
						url: "exampapertopic/examinationDelete",
						method: "post",
						data: ids
					}).then(({
						data
					}) => {
						if (data && data.code === 0) {
							this.$message({
								message: "操作成功",
								type: "success",
								duration: 1500,
								onClose: () => {
									this.search();
								}
							});
						} else {
							this.$message.error(data.msg);
						}
					});
				});
			},
			// 导入功能上传文件成功后调用导入方法
			exampapertopicUploadSuccess(data) {
				let _this = this;
				_this.$http({
					url: "exampapertopic/batchInsert?fileName=" + data.file,
					method: "get"
				}).then(({
					data
				}) => {
					if (data && data.code === 0) {
						_this.$message({
							message: "导入试卷选题数据成功",
							type: "success",
							duration: 1500,
							onClose: () => {
								_this.search();
							}
						});
					} else {
						_this.$message.error(data.msg);
					}
				});

			},
			// 导入功能上传文件失败后调用导入方法
			exampapertopicUploadError(data) {
				this.$message.error('上传失败');
			},
		}
	};
</script>
<style lang="scss" scoped>
//导出excel
.export-excel-wrapper{
    display: inline-block;
}
.slt {
    margin: 0 !important;
    display: flex;
}

.ad {
    margin: 0 !important;
    display: flex;
}

.pages {
& /deep/ el-pagination__sizes{
& /deep/ el-input__inner {
      height: 22px;
      line-height: 22px;
  }
}
}


.el-button+.el-button {
    margin:0;
}

.tables {
& /deep/ .el-button--success {
      height: 40px;
      color: rgba(0, 0, 0, 1);
      font-size: 14px;
      border-width: 0 5px 5px;
      border-style: solid;
      border-color: var(--publicSubColor);
      border-radius: 0;
      background-color: var(--publicMainColor);
  }

& /deep/ .el-button--primary {
      height: 40px;
      color: rgba(0, 0, 0, 1);
      font-size: 14px;
      border-width: 0 5px 5px;
      border-style: solid;
      border-color: var(--publicSubColor);
      border-radius: 0;
      background-color: var(--publicMainColor);
  }

& /deep/ .el-button--danger {
      height: 40px;
      color: rgba(0, 0, 0, 1);
      font-size: 14px;
      border-width: 0 5px 5px;
      border-style: solid;
      border-color: var(--publicSubColor);
      border-radius: 0;
      background-color: var(--publicMainColor);
  }

& /deep/ .el-button {
      margin: 4px;
  }
}
.form-content {
    background: transparent;
}
.table-content {
    background: transparent;
}

.tables /deep/ .el-table__body tr {
    background-color: rgba(255, 255, 255, 1) !important;
    color: rgba(0, 0, 0, 1) !important;
}
.tables /deep/ .el-table__body tr.el-table__row--striped td {
    background: transparent;
}
.tables /deep/ .el-table__body tr.el-table__row--striped {
    background-color: var(--publicMainColor) !important;
    color: rgba(0, 0, 0, 1) !important;
}

.tables /deep/ .el-table__body tr:hover>td {
    background-color: #f5f5f5 !important;
    color: #333 !important;
}</style>

</style>


