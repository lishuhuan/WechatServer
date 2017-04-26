/*
	页面说明： 启动页面 和 初始化依赖包
	-----------------------------------------------
  -----------------------------------------------
  -----------------------------------------------
  -----------------------------------------------
  -----------------------------------------------
*/
var f7 = new Framework7({
  modalTitle: "植物灯",
  modalButtonOk: "确定",
  modalButtonCancel: "取消",
  swipeBackPage: false,
  tapHold: true
});
var $$ = Dom7;
var mainView = f7.addView('.view-main', {
  dynamicNavbar: true,
  domCache: true
});
var url = window.location.href;
api.wxConfig(url, function(data) {
  if (data.result_code == 0) {
    wx.config({
      debug: false,
      appId: data.data.appid,
      timestamp: data.data.timestamp,
      nonceStr: data.data.nonceStr,
      signature: data.data.signature,
      jsApiList: ["onMenuShareTimeline", "onMenuShareAppMessage"]
    });
  }
});

function share(deviceId, time) {
  wx.onMenuShareTimeline({
    title: '我的植物灯',
    link: 'http://www.shuaikang.iego.cn/share?time=' + time + '&id=' + deviceId,
    imgUrl: 'http://121.40.65.146/flower_photo/huapen.png',
    success: function() {
      console.log("timeline success");
    },
    cancel: function() {
      console.log("timeline cancel");
    }
  });
  wx.onMenuShareAppMessage({
    title: '我的植物灯',
    desc: '这是我的植物灯，你也一起来吗？',
    link: 'http://www.shuaikang.iego.cn/share?time=' + time + '&id=' + deviceId,
    imgUrl: 'http://121.40.65.146/flower_photo/huapen.png',
    type: 'link',
    success: function() {
      console.log("appmessage success");
    },
    cancel: function() {
      console.log("appmessage cancel");
    }
  });
};

function stopBubble(e) {
  if (e && e.stopPropagation) {
    e.stopPropagation();
  } else {
    window.event.cancelBubble = true;
  }
};



/*
	页面说明： 主页面
	-----------------------------------------------
  -----------------------------------------------
  -----------------------------------------------
  -----------------------------------------------
  -----------------------------------------------
*/
var _list = [];
var _ylist = [];
f7.onPageBeforeInit('home', function() {
  setTimeout(function() {
    $$('.launch').css('-webkit-transitionDuration', '0.5s');
    $$('.launch').css('-webkit-transform', 'scale(0,0)');
    setTimeout(function() {
      $$('.launch').hide();
    }, 500);
  }, 500);
  api.getDevice(api.getOpenId(), function(data) {
    data.forEach(function(value, index, array) {
      if (value.isHost == 0) {
        _list.push(value);
      } else {
        _ylist.push(value);
      }
    });
    $$('#idevice').html(initUI(_list, 'i'));
    $$('#ydevice').html(initUI(_ylist, 'y'));
    f7.accordionOpen(document.getElementsByClassName('accordion-item')[_list.length > 0 ? 0 : 1]);
    addAction();
    getDeviceStatus(_list, _ylist);
    $$('.swipeout').css('-webkit-transitionDuration', '0.2s');
    $$('.swipeout-actions-right').css('-webkit-transitionDuration', '0.2s');
    setTimeout(function() {
      $$('.swipeout-content').css('-webkit-transform', 'translateX(-10px)');
      $$('.swipeout-actions-right').css('-webkit-transform', 'translateX(80px)');
    }, 200);
    setTimeout(function() {
      $$('.swipeout-content').css('-webkit-transform', 'translateX(10px)');
      $$('.swipeout-actions-right').css('-webkit-transform', 'translateX(100px)');
    }, 400);
    setTimeout(function() {
      $$('.swipeout-content').css('-webkit-transform', 'translateX(-10px)');
      $$('.swipeout-actions-right').css('-webkit-transform', 'translateX(80px)');
    }, 600);
    setTimeout(function() {
      $$('.swipeout-content').css('-webkit-transform', 'translateX(10px)');
      $$('.swipeout-actions-right').css('-webkit-transform', 'translateX(100px)');
    }, 800);
    setTimeout(function() {
      $$('.swipeout-content').css('-webkit-transform', 'translateX(0px)');
      $$('.swipeout-actions-right').css('-webkit-transform', 'translateX(90px)');
    }, 1000);

  });

  $$('.accordion-item').on('opened', function() {});

  $$('.accordion-item').on('closed', function(e) {});
}).trigger();

function getDeviceStatus(list, ylist) {
  api.getDeviceListStatus(_openId, function(data) {
    var icount = 0,
      ycount = 0,
      iall = list.length,
      yall = ylist.length;
    var idevicestatue = document.getElementsByClassName('idevicestatue');
    var ydevicestatue = document.getElementsByClassName('ydevicestatue');
    for (var i in list) {
      for (var j in data) {
        if (list[i].deviceId == data[j].deviceId) {
          idevicestatue[i].innerText = data[j].status == '1' ? '状态: 在线' : '状态: 离线';
          data[j].status == "1" ? icount++ : '';
          break;
        }
      }
    }
    for (var i in ylist) {
      for (var j in data) {
        if (ylist[i].deviceId == data[j].deviceId) {
          ydevicestatue[i].innerText = data[j].status == '1' ? '状态: 在线' : '状态: 离线';
          data[j].status == "1" ? ycount++ : '';
          break;
        }
      }
    }

    $$('.iolcount').text('我的设备: ' + '(' + icount + '/' + iall + ')');
    $$('.yolcount').text('好友设备: ' + '(' + ycount + '/' + yall + ')');
  });
};

function initUI(data, key) {
  var html = '';
  if (typeof data != 'object')
    return html;
  data.forEach(function(e, index) {
    html += creatItem(e, index, key);
  });
  return html;
};

function creatItem(item, index, key) {
  return ('<li class="swipeout">' +
    '<div class="item-content swipeout-content">' +
    '<div class="device_item" index="' + index + '" key="' + key + '">' +
    '<div class="head_img">' +
    '<div class="img">' +
    '<img src="' + item.photo + '"></div>' +
    '<div class="part">' +
    '<div class="title">' + item.nickName + '</div>' +
    '<div class="edit" index="' + index + '" key="' + key + '">' +
    '<img src="ui/device_list/xgmc.png"></div>' +
    '</div>' +
    '<div class="part">' +
    '<div class="text ' + (key == 'i' ? 'idevicestatue' : 'ydevicestatue') + '">' + (item.status == '1' ? '状态: 在线' : '状态: 离线') + '</div>' +
    '</div>' +
    '<div class="operat">' +
    '<div class="img share" index="' + index + '" key="' + key + '">' +
    '<img src="ui/device_list/fx.png"></div>' +
    '</div>' +
    '</div>' +
    '</div>' +
    '</div>' +
    '<div class="swipeout-actions-right">' +
    '<a href="#" class="delect_device bg-red" index="' + index + '" key="' + key + '">删除</a>' +
    '</div>' +
    '</li>')
};

function addAction() {

  $$('.device_item').on('click', gotoDeviceInfo);
  $$('.swipeout').on('open', function(e) {
    $$('.device_item').off('click', gotoDeviceInfo);
  });
  $$('.swipeout').on('closed', function(e) {
    $$('.device_item').on('click', gotoDeviceInfo);
  });

  function gotoDeviceInfo(e) {
    stopBubble(e);
    var array = [];
    if (e.currentTarget.attributes.key.value == 'i')
      array = _list;
    else
      array = _ylist;
    mainView.router.load({
      url: "templates/device.html",
      query: {
        item: array[e.currentTarget.attributes.index.value],
        key: e.currentTarget.attributes.key.value
      }
    });
  };

  $$('.edit').click(function(e) {
    stopBubble(e);
    if (e.currentTarget.attributes.key.value == 'i')
      var deviceId = _list[e.currentTarget.attributes.index.value].deviceId;
    else {
      var deviceId = _ylist[e.currentTarget.attributes.index.value].deviceId;
    }
    f7.prompt('请输入需要修改的名字', function(value) {
      api.setDeviceNickName(deviceId, value, api.getOpenId(), function(data) {
        if (data)
          $$(e.target).parent().prev().text(value);
      });
    });
  });
  $$('.share').click(function(e) {
    stopBubble(e);
    if (e.currentTarget.attributes.key.value == 'i')
      var deviceId = _list[e.currentTarget.attributes.index.value].deviceId;
    else {
      var deviceId = _ylist[e.currentTarget.attributes.index.value].deviceId;
    }
    var time = String(new Date().getTime());
    api.qrcodeForShareApi(time, deviceId, function(data) {
      if (data.result_code == 0) {
        $$('.share-item').show();
        document.getElementById("qrCode").innerHTML = "";
        jQuery('#qrCode').qrcode(data.data);
        share(deviceId, time);
      }
    });
  });
  $$('.selectRepeatTime').click(function(e) {
    stopBubble(e);
    $$('.share-item').hide();
  });
  $$('.delect_device').click(function(e) {
    stopBubble(e);
    if (e.currentTarget.attributes.key.value == 'i')
      var deviceId = _list[e.currentTarget.attributes.index.value].deviceId;
    else {
      var deviceId = _ylist[e.currentTarget.attributes.index.value].deviceId;
    }
    f7.confirm('要解绑设备吗？', function() {
      api.unbind(deviceId, api.getOpenId(), function(data) {
        if (data.base_resp.errcode == 0) {
          api.getDevice(_openId, function(data) {
            if (e.target.attributes.key.value == 'i') {
              _list = data;
              $$('#idevice').html(initUI(data, 'i'));
            } else {
              _ylist = data;
              $$('#ydevice').html(initUI(data, 'y'));
            }
            $$('.device_item').off('click', gotoDeviceInfo);
            addAction();
            f7.accordionOpen(document.getElementsByClassName('accordion-item')[0]);
          });
        } else {
          f7.alert("解绑失败，错误原因： " + data.base_resp.errmsg);
        }
      })
    });
  });
};



/*
	页面说明： 设备控制页面
	-----------------------------------------------
  -----------------------------------------------
  -----------------------------------------------
  -----------------------------------------------
*/
f7.onPageBeforeInit('deviceInfo', function(e) {
  theme(e);
  current_device = e.query.item;
  deviceSyncStatus(e);
  addDeviceAction(e);
});
f7.onPageBack('deviceInfo', function() {
  device_isOpen = false;
  device_light = 0;
  device_changeLightIsOpen = false;
  current_device;
});
var device_isOpen = false;
var device_light = 0;
var device_changeLightIsOpen = false;
var current_device;

function theme(e) {
  var now = (new Date()).getHours();
  $$('.device_page').css('background', now < 18 && now > 6 ? "url('./ui/home/bj.png') no-repeat" : "url('./ui/home/w_bj.png') no-repeat");
  $$('.device_page').css('background-size', "100% 100%");
  $$('.sunImg').attr('src', now < 18 && now > 6 ? "ui/home/yg.png" : "ui/home/w_yg.png");
  $$('#kettle').attr('src', now < 18 && now > 6 ? "ui/home/sh.png" : "ui/home/w_sh.png");
  $$('#smallkettle').attr('src', now < 18 && now > 6 ? "ui/home/sh.png" : "ui/home/w_sh.png");
  $$('#contentTable').attr('src', now < 18 && now > 6 ? "ui/home/gz.png" : "ui/home/w_gz.png");
  $$('#footTable').attr('src', now < 18 && now > 6 ? "ui/home/zz.png" : "ui/home/w_zz.png");
  $$('.lightImg').attr('src', now < 18 && now > 6 ? "ui/home/gd.png" : "ui/home/w_gd.png");
  $$('.lightBtn').attr('src', now < 18 && now > 6 ? "ui/home/g.png" : "ui/home/g.png");
};

function deviceSyncStatus(e) {
  getUserInfo();
  if (e.query.key == 'y')
    $$('.setting').hide();
  api.getTempAndHumidity(e.query.item.deviceId, function(data) {
    $$('.device_humidity').text('湿度: ' + (data.humidity != undefined ? data.humidity + '%' : '...'));
    $$('.device_temperature').text('温度: ' + (data.temperature != undefined ? data.temperature + '℃' : '...'));
  });
  api.getRecentOperationRecord(e.query.item.deviceId, function(data) {
    var open = (data[1].action).substr(8, data[1].action.length - 7);
    $$('.input_light').text('灯光强度: ' + open);
    device_light = open;
    open > 0 ? openSync() : '';
  });
  api.operationUnread(api.getOpenId(), e.query.item.deviceId, function(data) {
    var number = Number(data.my) + Number(data.friends);
    $$('.count').text(number > 99 ? "99+" : number);
    if (number == 0 || number == undefined)
      $$('.count').css('display', 'none');
  });
  api.getSunPower(e.query.item.deviceId, function(data) {
    $$('.device_sunnumber').text("X" + (data.result_code == 0 ? data.data.length : 0));
    if (data.data.length == 0 || data.data.length == undefined)
      $$('.sun').css('display', 'none');
  });
};
var user;

function getUserInfo() {
  api.indexUser(api.getOpenId(), function(data) {
    user = data;
    $$('.device_userhead').attr('src', data.headimgurl ? data.headimgurl : "");
    $$('.device_username').text(data.nickName ? data.nickName : "");
    //   $$('.device_sunnumber').text("X" + data.memberPoint ? data.memberPoint : 0);
    if (data.memberPoint > 100)
      $$('.fillbg').css('width', 'calc(100% - 2px)');
    else
      $$('.fillbg').css('width', data.memberPoint ? 'calc("' + data.memberPoint + '"% - 2px)' : 'calc(0% - 2px)');
  });
};

function addDeviceAction(e) {

  $$('.changeLightBtn').click(function(event) {
    if (!device_isOpen) {
      f7.alert("请先开灯");
    } else {
      device_changeLightIsOpen = !device_changeLightIsOpen;
      $$('.changeLight').transition(200);
      $$('.changeLight').transform(device_changeLightIsOpen ? 'translate3d(200px, 0, 0)' : 'translate3d(0px, 0, 0)');
    }
  });
  $$('#input_light').change(function(event) {
    var light = $$('#input_light').val();
    device_light = light;
    $$('.input_light').text('灯光强度: ' + light);
    api.controlDevice(api.getOpenId(), e.query.item.deviceId, 'led_pwm-' + light, function(data) {
      if (data.asyErrorCode == 0)
        device_isOpen = light != 0 ? true : false;
      else if (data.asyErrorCode == 1)
        f7.alert("没有控制权限");
      else if (data.asyErrorCode == 2)
        f7.alert("未在设定时间内");
      else if (data.asyErrorCode == 3)
        f7.alert("设备离线");
      else if (data.asyErrorCode == 4)
        f7.alert("设备未响应");
    });
  });
  $$('.device_open').click(function(event) {
    openSync();
    var body = device_isOpen ? device_light : 0;
    if (!device_isOpen) {
      $$('.changeLight').transition(200);
      $$('.changeLight').transform('translate3d(0px, 0, 0)');
    }
    api.controlDevice(api.getOpenId(), e.query.item.deviceId, 'led_pwm-' + body, function(data) {
      if (data.asyErrorCode == 0) {} else if (data.asyErrorCode == 1) {
        f7.alert("没有控制权限");
        openSync();
      } else if (data.asyErrorCode == 2) {
        f7.alert("未在设定时间内");
        openSync();
      } else if (data.asyErrorCode == 3) {
        f7.alert("设备离线");
        openSync();
      } else if (data.asyErrorCode == 4) {
        f7.alert("设备未响应");
        openSync();
      } else {
        openSync();
      }
    });
  });
  $$('.sun').click(function(event) {
    api.collectPower(e.query.item.deviceId, api.getOpenId(), function(data) {
      if (data.result_code == 0) {
        $$('.sunImg').toggleClass("end");
        setTimeout(function() {
          $$('.sun').css('display', 'none');
        }, 2000);
        getUserInfo();
      }else{
        f7.alert('你已经收集过阳光了');
      }
    });
  });

  $$('.toolbarBtn').click(function(event) {
    var tool = event.currentTarget.attributes.key.value;
    switch (tool) {
      case '0':
        {
          mainView.router.load({
            url: 'templates/setting.html',
            query: current_device
          });
          break;
        }
      case '1':
        {
          mainView.router.back();
          break;
        }
      case '2':
        {
          mainView.router.load({
            url: 'templates/friend.html',
            query: current_device
          });
          break;
        }
      case '3':
        {
          mainView.router.load({
            url: 'templates/infoBook.html',
            query: current_device
          });
          break;
        }
      default:
        {

        }
    }
  });

  $$('#kettle').click(function(event) {
    Watering();
  });
  $$('.water').click(function(event) {
    Watering();
  });

  function Watering() {
    $$('.watering').addClass('watershow');
    $$('.water').addClass('waterhide');
    $$('.kettle').addClass('waterhide');
    setTimeout(function() {
      $$('.watering').removeClass('watershow');
      setTimeout(function() {
        $$('.water').removeClass('waterhide');
        $$('.kettle').removeClass('waterhide');
      }, 500);
    }, 2000);
    api.controlDevice(api.getOpenId(), e.query.item.deviceId, "watering-5", function(data) {
      if (data.asyErrorCode == 0) {
        f7.alert("浇水成功");
      } else if (data.asyErrorCode == 1) {
        f7.alert("没有控制权限");
      } else if (data.asyErrorCode == 2) {
        f7.alert("不在浇水时间段或者有人浇过水");
      } else if (data.asyErrorCode == 3) {
        f7.alert("设备离线");
      } else if (data.asyErrorCode == 4) {
        f7.alert("设备未响应");
      }
    });

  };

};

function openSync() {
  var now = (new Date()).getHours();
  device_isOpen = !device_isOpen;
  $$('.lightImg').attr('src', device_isOpen ? (now < 18 && now > 6 ? "ui/home/kd.png" : "ui/home/w_kd.png") : (now < 18 && now > 6 ? "ui/home/gd.png" : "ui/home/w_gd.png"));
  $$('.lightBtn').attr('src', device_isOpen ? "ui/home/k.png" : "ui/home/g.png");
};



/*
	页面说明： 消息页面
	-----------------------------------------------
  -----------------------------------------------
  -----------------------------------------------
  -----------------------------------------------
*/
f7.onPageBeforeInit('infoBook', function(e) {
  var loading = false;
  getStatusInfo(e, loading);
  f7.detachInfiniteScroll($$('.infinite-scroll'));
  $$('.infinite-scroll-preloader').remove();
  $$('.infinite-scroll').on('infinite', function() {
    if (loading) return;
    loading = true;
    if (curInfoCount >= allInfoCount) {
      f7.detachInfiniteScroll($$('.infinite-scroll'));
      return;
    };
    pageNumber = Number(pageNumber) + 1;
    getStatusInfo(e, loading);
  });
});
f7.onPageBack('infoBook', function() {
  infoList = [];
  allInfoCount = 0;
  curInfoCount = 0;
  pageNumber = '1';
});

var infoList = [];
var allInfoCount = 0;
var curInfoCount = 0;
var pageNumber = '1';

function getStatusInfo(e, loading) {
  api.getDeviceStatus_status(e.query.deviceId, '1', api.getOpenId(), Number(pageNumber), function(data) {
    loading = false;
    for (var i in data.status) {
      if (data.status[i].action.indexOf("led_pwm") != -1) {
        data.status[i]["actionTitle"] = "开了灯";
      } else {
        data.status[i]["actionTitle"] = "洒了水";
      }
      var str = data.status[i].time.replace(" ", "T");
      var time = new Date(Date.parse(str));
      var month = (time.getUTCMonth() + 1) < 10 ? "0" + (time.getUTCMonth() + 1) : (time.getUTCMonth() + 1);
      var day = time.getUTCDate() < 10 ? "0" + time.getUTCDate() : time.getUTCDate();
      data.status[i]["month"] = month;
      data.status[i]["day"] = day
    }
    infoList = infoList.concat(data.status);

    curInfoCount += data.status.length;
    if (pageNumber === '1')
      allInfoCount = data.count;

    infoList.forEach(function(value, index) {
      $$('.infoList').append(creatInfoItem(value));
    });
  });
};

function creatInfoItem(item) {
  return '<li class="item-content">' +
    '<div class="item-inner">' +
    '<div class="item-title" style="width: 100%;height: 50px;margin-left: 10px;">' +
    '<div class="img" style="float: left;">' +
    '<img src="' + item.photoPath + '" style="width: 50px;border-radius: 5px;border-radius: 5px;">' +
    '</div>' +
    '<div class="infoImg" style="float: left;width: 10px;margin-top: 18px;">' +
    '<img src="ui/device_setting/' + (user.nickName == item.username ? 'infobook_b' : 'infobook') + '.png" style="width: 100%;">' +
    '</div>' +
    '<div class="part" style="width: auto;height: 50px;border-radius: 10px;position: absolute;left: 60px;right: 0px;background-color: ' + (user.nickName == item.username ? '#87CEFA' : '#FDCDCD') + ';">' +
    '<div class="title" style="float: left;margin-left: 10px;margin-top: 5px;font-size: 13px;color: #6286C3;">' + item.username + '&emsp;&emsp;</div>' +
    '<div class="title" style="font-size: 14px;margin-top: 5px;margin-left: 10px;color: #6286C3;">' + item.time + '</div>' +
    '<div class="content" style="width: 90%;margin-left: 5%;font-size: 13px;overflow: hidden; text-overflow: ellipsis; word-spacing: nowrap;">HI!今天我帮你把植物' + item.actionTitle + '哦~是不是棒棒哒~</div>' +
    '</div>' +
    '</div>' +
    '</div>' +
    '</li>'
};



/*
	页面说明： 好友页面
	-----------------------------------------------
  -----------------------------------------------
  -----------------------------------------------
  -----------------------------------------------
*/
f7.onPageBeforeInit('friendPage', function(e) {
  getFriendList(e);
});
f7.onPageBack('friendPage', function() {

});

function getFriendList(e) {
  api.getFriendList(api.getOpenId(), e.query.deviceId, function(data) {
    var people = new Array();
    for (var i in data) {
      if (data[i].isControl == "2") {
        data[i].isControl = true;
        people.push(data[i]);
      } else if (data[i].isControl == "1") {
        data[i].isControl = false;
        people.push(data[i]);
      } else if (data[i].isControl == "0") {
        data[i].isControl = e.query.isHost == 1 ? true : false;
        $$('.hostInfo').html(creatFriendItem(data[i], true));
      }
    }
    people.forEach(function(value, index) {
      $$('.hostInfo').append(creatFriendItem(value, false));
    });
    friendAction(e);
  });
};

function creatFriendItem(item, isHost) {
  return '<li class="friend_item">' +
    '<div class="head_img">' +
    '<div class="img">' +
    '<img src="' + item.headimgurl + '"></div>' +
    '</div>' +
    '<div class="item-title">' + item.nickName + (isHost ? '<span style="background-color: #FABE00;border-radius: 5px;color: white;font-size: 14px;padding: 3px;">主人</span>' : '') + '</div>' +
    '<div class="button" style="width: 80px;height: 30px;float: right;margin-top: 25px;margin-right: 20px;border: 0px;">' +
    (isHost ? (!item.isControl ? '<button class="hostBtn" style="width: 100%;height: 100%;border: 1px solid #42C8FE;border-radius: 5px;background-color: white;color:#42C8FE;" key="0" oid="' + item.openId + '">申请授权</button>' :
      '<button class="hostBtn" style="width: 100%;height: 100%;border: 1px solid gray;border-radius: 5px;background-color: white;color:gray;" key="1" oid="' + item.openId + '">已授权</button>' +
      '</div>') : (!item.isControl ? '<button class="friendBtn" style="width: 100%;height: 100%;border: 1px solid #d5d5d5;border-radius: 5px;background-color: white;color:#d5d5d5;" key="0" oid="' + item.openId + '">未授权</button>' :
      '<button class="friendBtn" style="width: 100%;height: 100%;border: 1px solid gray;border-radius: 5px;background-color: white;color:gray;" key="1" oid="' + item.openId + '">已授权</button>')) +
    '</li>';
};

function friendAction(e) {
  $$('.hostBtn').click(function(event) {
    if (e.query.isHost != "0") {
      f7.alert("您没有操作权限");
    } else {
      api.applyAuth(event.target.attributes.oid.value, e.query.deviceId, function(data) {
        if (data) {
          $$(event.target).text('已授权');
        }
      });
    }
  });
  $$('.friendBtn').click(function(event) {
    if (e.query.isHost != "0") {
      f7.alert("您没有操作权限");
    } else {
      api.handleDeviceApply(event.target.attributes.oid.value, e.query.deviceId, event.target.attributes.key.value == '0' ? "2" : "1", function(data) {
        if (data) {
          $$(event.target).text('已授权');
        }
      });
    }
  });
};



/*
	页面说明： 设置页面
	-----------------------------------------------
  -----------------------------------------------
  -----------------------------------------------
  -----------------------------------------------
*/
var pickerInline;
f7.onPageBeforeInit('settingPage', function(e) {
  creatPicker();
  syncSettingStatus(e);
  settingAction(e);
});
f7.onPageBack('settingPage', function() {});

function creatPicker() {
  var today = new Date();
  pickerInline = f7.picker({
    container: '#picker-date-container',
    toolbarTemplate: '<div class="toolbar">' +
      '<div class="toolbar-inner">' +
      '<div class="left">' +
      '</div>' +
      '<div class="right rightPickerButton">' +
      '<a class="link">确定</a>' +
      '</div>' +
      '</div>' +
      '</div>',
    value: [today.getHours(), (today.getMinutes() < 10 ? '0' + today.getMinutes() : today.getMinutes())],
    onChange: function(picker, values, displayValues) {
      console.log(displayValues);
    },

    formatValue: function(p, values, displayValues) {
      return values[0] + ':' + values[1];
    },

    cols: [{
      values: (function() {
        var arr = [];
        for (var i = 0; i <= 23; i++) {
          arr.push(i);
        }
        return arr;
      })(),
    }, {
      divider: true,
      content: ':'
    }, {
      values: (function() {
        var arr = [];
        for (var i = 0; i <= 59; i++) {
          arr.push(i < 10 ? '0' + i : i);
        }
        return arr;
      })(),
    }]
  });
  $$('.rightPickerButton').click(function(event) {
    if (waterEditShow) {
      $$('#input1').val(pickerInline.value[0].substring(0, 1));
      $$('#input2').val(pickerInline.value[0].substring(1, 2));
      $$('#input3').val(pickerInline.value[1].substring(0, 1));
      $$('#input4').val(pickerInline.value[1].substring(1, 2));
    } else {
      $$('#input5').val(pickerInline.value[0].substring(0, 1));
      $$('#input6').val(pickerInline.value[0].substring(1, 2));
      $$('#input7').val(pickerInline.value[1].substring(0, 1));
      $$('#input8').val(pickerInline.value[1].substring(1, 2));
    }
    $$('#picker-date-container').hide();
  });

  $$('#picker-date-container').hide();
};

var colorLine = [];
var waterLine = [];

function syncSettingStatus(e) {
  $$('#lightType').val(e.query.sunModel == 1 ? false : true);
  $$('#colorTem').val(e.query.colorTem);
  syncled(e);
  syncWater(e);
};

function syncled(e) {
  api.getLedProperty(e.query.deviceId, function(data) {
    if (data.result_code == 0) {
      for (var i in data.data) {
        var date = new Date(data.data[i].time.time);
        data.data[i]["time"] = (date.getHours() < 10 ? "0" + date.getHours() : date.getHours()) + ":" + (date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes());
        date.setMinutes(date.getMinutes() + Number(data.data[i].duration));
        data.data[i]["time2"] = (date.getHours() < 10 ? "0" + date.getHours() : date.getHours()) + ":" + (date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes());
        data.data[i]["time3"] = data.data[i]["model"] == "0" ? "入睡模式" : "起床模式";
        data.data[i]["time3"] += "        每周" + time(data.data[i].repeat_date);
      }
      (data.data).forEach(function(value, index, array) {
        $$('.colorItems').append(creatColorItem(value, index));
      });
      $$('#colorTem').val(e.query.colorTem);
      $$('#lightType').prop('checked', e.query.sunModel == 1 ? false : true);
      colorLine = data.data;
      $$('.colorItem').click(function(event) {
        if (event.currentTarget.attributes.clicktype.value == "edit") {
          var r = colorLine[event.currentTarget.attributes.index.value];
          f7.confirm('要删除这条设置吗？', function() {
            api.deleteLedProperty(r.id, function(data) {
              if (data) {
                $$('.colorItems').html('');
                syncled(e);
              }
            });
          });
        }
      });
    }
  });
};

function syncWater(e) {
  api.getDeviceProperty(e.query.deviceId, function(data) {
    for (var i in data) {
      var date = new Date(data[i].timePoint);
      data[i]["time"] = (date.getHours() < 10 ? "0" + date.getHours() : date.getHours()) + ":" + (date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes());
      date.setMinutes(date.getMinutes() + 30);
      data[i]["time2"] = (date.getHours() < 10 ? "0" + date.getHours() : date.getHours()) + ":" + (date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes());
      data[i]["time3"] = "每" + time(data[i].repetition);
    }
    data.forEach(function(value, index, array) {
      $$('.watchItems').append(creatWaterItem(value, index));
    });
    waterLine = data;
    $$('.waterItem').click(function(event) {
      if (event.currentTarget.attributes.clicktype.value == "edit") {
        var r = waterLine[event.currentTarget.attributes.index.value];
        f7.confirm('要删除这条设置吗？', function() {
          api.deleteDeviceProperty(r.id, function(data) {
            if (data) {
              $$('.watchItems').html('');
              syncWater(e);
            }
          });
        });
      }
    });
  });
};

function time(body) {
  var str = "";
  for (var i = 0; i < body.length; i++) {
    var object = body.substr(i, 1);
    if (object == '1') {
      str = str + "一 ";
    } else if (object == '2') {
      str = str + "二 ";
    } else if (object == '3') {
      str = str + "三 ";
    } else if (object == '4') {
      str = str + "四 ";
    } else if (object == '5') {
      str = str + "五 ";
    } else if (object == '6') {
      str = str + "六 ";
    } else if (object == '7') {
      str = str + "日 ";
    }
  }
  return str;
};

function creatColorItem(item, index) {
  return '<div class="text colorItem" clicktype="edit" type="color" index="' + index + '">' + item.time + '--' + item.time2 + '</div>' +
    '<div style="font-size: 10px;margin-left:20px;margin-top: -5px;color: gray;margin-top: -15px;margin-bottom: 10px;">' +
    item.time3 + '</div>';
};

function creatWaterItem(item, index) {
  return '<div class="text waterItem" clicktype="edit" type="water" index="' + index + '">' + item.time + ' ~ ' + item.time2 +
    '<span style="font-size: 10px;">' + item.time3 + '</span></div>';
};

var colorEditShow = false;
var waterEditShow = false;

function settingAction(e) {
  $$('#lightType').click(function(event) {
    var isChecked = $$('#lightType').prop('checked');
    api.sunPowerPlan(e.query.deviceId, isChecked ? "0" : "1", function(data) {
      if (data.result_code != 0)
        f7.alert("设置失败");
    });
  });
  $$('#colorTem').change(function(event) {
    var inputVal = $$('#colorTem').val();
    api.updatePowerAndCt(e.query.deviceId, '1', String(inputVal), function(data) {
      if (data.result_code != 0)
        f7.alert("设置失败");
    });
  });
  $$('.colorTimeAdd').click(function(event) {
    if (!colorEditShow) {
      colorEditShow = true;
      showEdit('addColorTime');
    }
  });
  $$('.waterTimeAdd').click(function(event) {
    if (!waterEditShow) {
      waterEditShow = true;
      showEdit('addWaterTime');
    }
  });
  $$('.editFootButton').click(function(event) {
    switch (event.target.attributes.type.value) {
      case 'waterNo':
        {
          hideEdit('addWaterTime');
          break;
        }
      case 'waterYes':
        {
          var time1 = $$('#input1').val();
          var time2 = $$('#input2').val();
          var time3 = $$('#input3').val();
          var time4 = $$('#input4').val();
          var repDay = $$('.addTimeSub').text();
          var slide = $$('#input_waterTime').val();
          if (repDay == '每天') {
            repDay = "1234567"
          } else {
            var str = '';
            str += repDay.indexOf('一') > 0 ? '1' : '';
            str += repDay.indexOf('二') > 0 ? '2' : '';
            str += repDay.indexOf('三') > 0 ? '3' : '';
            str += repDay.indexOf('四') > 0 ? '4' : '';
            str += repDay.indexOf('五') > 0 ? '5' : '';
            str += repDay.indexOf('六') > 0 ? '6' : '';
            str += repDay.indexOf('日') > 0 ? '7' : '';
            repDay = str;
          }
          if (time1 == "" || time2 == "" || time3 == "" || time4 == "") {
            f7.alert("请输入时间");
          } else if (repDay == "") {
            f7.alert("请选择浇水的天数");
          } else {
            hideEdit('addWaterTime');
            api.setDeviceProperty(e.query.deviceId, api.getOpenId(), 'watering', time1 + time2 + ":" + time3 + time4 + ":00", repDay, String(slide), function(data) {
              if (data) {
                $$('.watchItems').html('');
                syncWater(e);
              }
            });
          }
          break;
        }
      case 'colorNo':
        {
          hideEdit('addColorTime');
          break;
        }
      case 'colorYes':
        {
          var time1 = $$('#input5').val();
          var time2 = $$('#input6').val();
          var time3 = $$('#input7').val();
          var time4 = $$('#input8').val();
          var repDay = $$('.addTimeSub').text();
          var type = $$('.addTypeSub').text();
          var slide = $$('#input_ColorTime').val();
          if (repDay == '每天') {
            repDay = "1234567"
          } else {
            var str = '';
            str += repDay.indexOf('一') > 0 ? '1' : '';
            str += repDay.indexOf('二') > 0 ? '2' : '';
            str += repDay.indexOf('三') > 0 ? '3' : '';
            str += repDay.indexOf('四') > 0 ? '4' : '';
            str += repDay.indexOf('五') > 0 ? '5' : '';
            str += repDay.indexOf('六') > 0 ? '6' : '';
            str += repDay.indexOf('日') > 0 ? '7' : '';
            repDay = str;
          }
          if (time1 == "" || time2 == "" || time3 == "" || time4 == "") {
            f7.alert("请输入时间");
          } else if (repDay == "") {
            f7.alert("请选择重复的天数");
          } else {
            hideEdit('addColorTime');
            api.sleepAndGetupLed(e.query.deviceId, api.getOpenId(), type == "伴你入睡" ? "0" : "1", String(slide), time1 + time2 + ":" + time3 + time4 + ":00", "0", String(repDay), function(data) {
              if (data) {
                $$('.colorItems').html('');
                syncled(e);
              }
            });
          }
          break;
        }
      default:
        {
          break;
        }
    }
  });
  $$('#input_waterTime').change(function(event) {
    var inputVal = $$('#input_waterTime').val();
    $$('.addWaterTimeSub2').text(inputVal + 's');
  });
  $$('.addTimeRep').click(function(event) {
    $$('.time1').show();
  });
  $$('.addTimeType').click(function(event) {
    $$('.type1').show();
  });
  $$('.time1Button').click(function(event) {
    if (event.currentTarget.attributes.index.value == '1') {
      $$('.addTimeSub').text('每天');
      $$('.time1').hide();
    } else {
      $$('.time1').hide();
      $$('.time2').show();
    }
  });
  $$('.time2Buttons').click(function(event) {
    var array = $$('.allDay>.part>.text');
    var dom = $$(array[event.currentTarget.attributes.index.value - 1]);
    if (dom.css('color') == 'rgb(0, 0, 0)') {
      dom.css('color', 'rgb(255, 255, 255)');
      dom.css('background-color', 'rgb(63, 155, 252)');
      dom.css('border-color', 'rgb(63, 155, 252)');
    } else {
      dom.css('color', 'rgb(0, 0, 0)');
      dom.css('background-color', 'rgb(255, 255, 255)');
      dom.css('border-color', 'rgb(0, 0, 0)');
    }
  });
  $$('.cusTimeButtons').click(function(event) {
    if (event.target.attributes.index.value == 'yes') {
      var array = $$('.allDay>.part>.text');
      var str = '每周';
      for (var i = 0; i < 7; i++) {
        var dom = $$(array[i]);
        if (dom.css('color') != 'rgb(0, 0, 0)') {
          str += ' ' + dataFormat(i);
        }
      }
      $$('.addTimeSub').text(str == '每周' ? '' : str);
    }
    $$('.time2').hide();

    function dataFormat(value) {
      if (value == 0)
        return '一';
      else if (value == 1)
        return '二';
      else if (value == 2)
        return '三';
      else if (value == 3)
        return '四';
      else if (value == 4)
        return '五';
      else if (value == 5)
        return '六';
      else if (value == 6)
        return '日';
    }
  });
  $$('.typeButton').click(function(event) {
    if (event.currentTarget.attributes.index.value == '1'){
      $$('.addTypeSub').text('伴你入睡');
      $$('.addTypeSub2').text('15min');
      $$('#input_ColorTime').val(15);
      $$('#input_ColorTime').prop('disabled', true);
    }else{
      $$('.addTypeSub').text('伴你起床');
      $$('#input_ColorTime').prop('disabled', false);
    }
    $$('.type1').hide();
  });
  $$('#input_ColorTime').change(function(event) {
    var inputVal = $$('#input_ColorTime').val();
    $$('.addTypeSub2').text(inputVal + 'min');
  });
  $$('.itmeInput').click(function(event) {
    $$('#picker-date-container').show();
  });

  function cleanEdit(classname) {
    if (classname == 'addWaterTime') {
      $$('#input1').val('');
      $$('#input2').val('');
      $$('#input3').val('');
      $$('#input4').val('');
      $$('.addWaterTimeSub2').text('5s');
      $$('#input_waterTime').val(5);
    } else {

    }
    $$('.addTimeSub').text('');
  }

  // function valueEdit(classname, object) {
  //   if (classname == 'addWaterTime') {
  //     $$('#input1').val('');
  //     $$('#input2').val('');
  //     $$('#input3').val('');
  //     $$('#input4').val('');
  //     $$('.addWaterTimeSub').text('5s');
  //     $$('#input_waterTime').val(5);
  //   }
  // }

  function showEdit(classname) {
    $$('.' + classname).show();
    setTimeout(function() {
      $$('.' + classname).transition(300);
      $$('.' + classname).transform('translate3d(0, -350px, 0)');
    }, 50);
  }

  function hideEdit(classname) {

    $$('.' + classname).transition(300);
    $$('.' + classname).transform('translate3d(0, 350px, 0)');
    setTimeout(function() {
      cleanEdit(classname);
      $$('.' + classname).hide();
      if (classname == 'addColorTime')
        colorEditShow = false;
      else
        waterEditShow = false
    }, 300);
  }
};