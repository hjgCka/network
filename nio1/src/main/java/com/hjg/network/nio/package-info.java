/**
 *
 * nio是一种同步非阻塞的I/O模型，读取数据是同步的(当然也是阻塞的)，工作线程会等待数据读取完毕再去做其它事情。
 * 非阻塞是指，检查channel状态不是阻塞的，会立刻返回。
 *
 * 异步IO在nio2中进行了引入，但是性能并不比nio号，所以没有普遍使用。
 *
 * @description:
 * @author: hjg
 * @createdOn: 2021/3/3
 */
package com.hjg.network.nio;