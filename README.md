# Bamboo

[![Download](https://api.bintray.com/packages/wulugongshe/maven/bamboo/images/download.svg) ](https://bintray.com/wulugongshe/maven/bamboo/_latestVersion)

## 跨进程数据库持久化系统，独创竹节数据结构，让读写更快速。解决Android中的原生数据库经常发生各种意外问题

### 码云地址：[Bamboo](https://gitee.com/836673942/Bamboo)

### 本次修改：
1. 修复中文存储错误
2. 新增测试demo

### android-studio gradle
``` 
  project：
    repositories {
     maven { url 'https://dl.bintray.com/wulugongshe/maven' }
                 }
  app：
    compile 'com.lazymc:bamboo:1.0.1'
```

### /**
     * <p>竹子数据持久化系统，创意来源：因数据结构像竹子的节一样而得名</p>
     * <p>该系统应该配合数据缓存使用，因为直接操作io，会降低系统性能</p>
     * <p>理论上该数据支持物理最大化存储，数据结构应当保持不变，不变的前提是初始化数据的长度要尽量的合理，否则导致后面的长度变长而
     * 导致性能下降</p>
     * <p>该系统容量扩展会根据内容增加而增加，但是大小基本上跟数据大小一致</p>
     * <p>使用方式：
     * 先初始化
     *
     * @see Bamboo.init()
     * 再获取服务
     * @see Bamboo.getBambooServer().remove(key);
     * @see Bamboo.getBambooServer().read(key);
     * @see Bamboo.getBambooServer().write(key, value);
     * @see Bamboo.getBambooServer().cut(key)
     * @see Bamboo.getBambooServer().clearRef()
     * <p>
     * 系统优缺点：
     * 优点，支持多进程，对增量写友好，不耗费多余资源，对文件操作快速（因为基于随机读写）
     * 缺点：扩展实体长度困难，收缩缓慢
     * </p>
     */

```
public interface IBambooServer {
    /**
     * 保存数据，会覆盖前面的数据
     *
     * @param key
     * @param data
     * @return
     * @throws Exception
     */
    boolean write(String key, String data) throws Exception;

    /**
     * 读取数据，读取失败或没有值返回空字符“”
     *
     * @param key
     * @return
     * @throws Exception
     */
    String read(String key) throws Exception;

    /**
     * 删除数据，标记删除
     *
     * @param key
     * @return
     * @throws Exception
     */
    boolean cut(String key) throws Exception;

    /**
     * 移除数据实体，该函数很消耗io和计算资源，若无必要请不要使用
     *
     * @param key
     * @return
     * @throws Exception
     */
    boolean remove(String key) throws Exception;

    /**
     * 清除垃圾，该函数会删除被标记为删除的数据实体，相当耗费资源，请使用线程运行
     *
     * @return
     * @throws Exception
     */
    boolean clearRef() throws Exception;

    /**
     * 返回因各种意外导致的关闭状态
     *
     * @return
     */
    boolean isClose();
}
```
