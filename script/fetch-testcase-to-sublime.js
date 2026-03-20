// ==UserScript==
// @name         算法题样例抓取器 (支持CF/vjudge/牛客/洛谷)
// @namespace    http://tampermonkey.net/
// @version      1.0
// @description  抓取Codeforces/vjudge/牛客/洛谷题目样例，一键复制为test/correct_answers格式，按钮可拖动
// @author       You
// @match        https://codeforces.com/contest/*/problem/*
// @match        https://codeforces.com/problemset/problem/*
// @match        https://vjudge.net/problem/*
// @match        https://ac.nowcoder.com/acm/contest/*
// @match        https://ac.nowcoder.com/acm/problem/*
// @match        https://www.luogu.com.cn/problem/*
// @icon         https://codeforces.com/favicon.ico
// @grant        none
// ==/UserScript==

(function() {
    'use strict';

    // 检测是否在 iframe 中
    const isInIframe = window.self !== window.top;

    // 如果在 vjudge 的 iframe 中，不执行任何操作
    if (isInIframe && window.location.hostname.includes('vjudge.net')) {
        console.log('在 vjudge iframe 中，跳过执行');
        return;
    }


    // 防止重复注入
    if (window.__cfSampleDragInjected) return;
    window.__cfSampleDragInjected = true;

    // 样式
    const styles = `
        #cf-sample-dragger {
            position: fixed;
            top: 120px;
            right: 30px;
            width: 56px;
            height: 56px;
            background-color: #2d7ee9;
            color: white;
            border-radius: 50%;
            box-shadow: 0 4px 12px rgba(0, 20, 80, 0.4);
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: grab;
            z-index: 999999;
            font-family: 'Segoe UI', Roboto, system-ui, sans-serif;
            font-size: 26px;
            font-weight: bold;
            user-select: none;
            transition: box-shadow 0.2s, transform 0.1s;
            border: 2px solid white;
            box-sizing: border-box;
            line-height: 1;
        }
        #cf-sample-dragger:active {
            cursor: grabbing;
            box-shadow: 0 6px 16px rgba(0, 0, 0, 0.3);
            transform: scale(0.96);
        }
        #cf-sample-dragger:hover {
            background-color: #1a5fc9;
        }
        .cf-toast-message {
            position: fixed;
            bottom: 24px;
            left: 50%;
            transform: translateX(-50%);
            background: #1e2b3c;
            color: #e4f0ff;
            padding: 8px 22px;
            border-radius: 40px;
            font-family: 'Segoe UI', monospace;
            font-size: 15px;
            box-shadow: 0 6px 20px rgba(0,0,0,0.3);
            z-index: 1000000;
            border: 1px solid #3f5e8c;
            pointer-events: none;
            transition: opacity 0.3s;
            white-space: pre;
        }
        .cf-loading {
            opacity: 0.7;
            pointer-events: none;
        }
    `;

    // 添加样式
    const styleEl = document.createElement('style');
    styleEl.textContent = styles;
    document.head.appendChild(styleEl);

    // 创建按钮
    const btn = document.createElement('div');
    btn.id = 'cf-sample-dragger';
    btn.innerText = '📋';
    btn.setAttribute('title', '拖动按钮 → 点击复制样例 (支持CF/vjudge/牛客/洛谷)');
    document.body.appendChild(btn);

    // 拖动相关变量
    let offsetX = 0, offsetY = 0;
    let isDragging = false;
    let startX = 0, startY = 0;
    let moved = false;

    // 鼠标按下
    btn.addEventListener('mousedown', (e) => {
        e.preventDefault();

        const rect = btn.getBoundingClientRect();
        startX = e.clientX;
        startY = e.clientY;
        offsetX = startX - rect.left;
        offsetY = startY - rect.top;

        isDragging = true;
        moved = false;
        btn.style.cursor = 'grabbing';
        btn.style.transition = 'none';
    });

    // 鼠标移动
    document.addEventListener('mousemove', (e) => {
        if (!isDragging) return;

        e.preventDefault();
        moved = true;

        const x = e.clientX - offsetX;
        const y = e.clientY - offsetY;

        // 边界限制
        const maxX = window.innerWidth - btn.offsetWidth;
        const maxY = window.innerHeight - btn.offsetHeight;
        const clampedX = Math.min(Math.max(x, 0), maxX);
        const clampedY = Math.min(Math.max(y, 0), maxY);

        btn.style.left = clampedX + 'px';
        btn.style.top = clampedY + 'px';
        btn.style.right = 'auto';
        btn.style.bottom = 'auto';
    });

    // 鼠标释放
    document.addEventListener('mouseup', (e) => {
        if (isDragging) {
            isDragging = false;
            btn.style.cursor = 'grab';
            btn.style.transition = 'box-shadow 0.2s, transform 0.1s';

            // 如果没有移动，触发点击逻辑
            if (!moved) {
                handleClick();
            }
        }
    });

    // 防止按钮被选中
    btn.addEventListener('dragstart', (e) => e.preventDefault());

    // 判断当前是哪个网站
    function getSite() {
        const hostname = window.location.hostname;
        if (hostname.includes('codeforces.com')) return 'cf';
        if (hostname.includes('vjudge.net')) return 'vjudge';
        if (hostname.includes('nowcoder.com')) return 'nowcoder';
        if (hostname.includes('luogu.com.cn')) return 'luogu';
        return 'unknown';
    }


    function removeRstrip(s){

    }

    // 等待元素加载的通用函数
    function waitForElement(selector, timeout = 10000, context = document) {
        return new Promise((resolve, reject) => {
            // 如果元素已经存在，直接返回
            const element = context.querySelector(selector);
            if (element) {
                resolve(element);
                return;
            }

            // 否则等待元素出现
            const startTime = Date.now();
            const observer = new MutationObserver((mutations, obs) => {
                const element = context.querySelector(selector);
                if (element) {
                    obs.disconnect();
                    resolve(element);
                } else if (Date.now() - startTime > timeout) {
                    obs.disconnect();
                    reject(new Error(`等待元素超时: ${selector}`));
                }
            });

            observer.observe(context, {
                childList: true,
                subtree: true
            });
        });
    }

    // 等待多个元素加载
    function waitForElements(selector, timeout = 10000, context = document) {
        return new Promise((resolve, reject) => {
            // 如果元素已经存在，直接返回
            const elements = context.querySelectorAll(selector);
            if (elements.length > 0) {
                resolve(elements);
                return;
            }

            // 否则等待元素出现
            const startTime = Date.now();
            const observer = new MutationObserver((mutations, obs) => {
                const elements = context.querySelectorAll(selector);
                if (elements.length > 0) {
                    obs.disconnect();
                    resolve(elements);
                } else if (Date.now() - startTime > timeout) {
                    obs.disconnect();
                    reject(new Error(`等待元素超时: ${selector}`));
                }
            });

            observer.observe(context, {
                childList: true,
                subtree: true
            });
        });
    }

    // 获取vjudge的iframe document
    async function getIframeDocument() {
        try {
            const iframe = document.getElementById('frame-description');
            if (!iframe) {
                throw new Error('未找到 iframe#frame-description');
            }

            // 等待iframe加载完成
            await new Promise((resolve, reject) => {
                if (iframe.contentDocument && iframe.contentDocument.readyState === 'complete') {
                    resolve();
                } else {
                    iframe.addEventListener('load', resolve);
                    setTimeout(() => reject(new Error('iframe加载超时')), 10000);
                }
            });

            return iframe.contentDocument || iframe.contentWindow.document;
        } catch (error) {
            console.error('获取iframe document失败:', error);
            return null;
        }
    }

    // 从vjudge提取样例
    async function extractVjudgeSamples() {
        try {
            showToast('⏳ 正在等待样例加载...');

            const iframeDoc = await getIframeDocument();
            if (!iframeDoc) {
                showToast('❌ 无法访问题目内容iframe');
                return null;
            }

            const sampleTables = await waitForElements('table.vjudge_sample', 15000, iframeDoc);

            if (!sampleTables || sampleTables.length === 0) {
                showToast('❌ 未找到 vjudge_sample 表格');
                return null;
            }

            const result = [];

            for (const table of sampleTables) {
                const rows = table.querySelectorAll('tbody tr');
                for (const row of rows) {
                    const tds = row.querySelectorAll('td');
                    if (tds.length >= 2) {
                        const inputTd = tds[0];
                        const outputTd = tds[1];

                        let inputText = '';
                        const inputPre = inputTd.querySelector('pre');
                        inputText = inputPre ? inputPre.innerText : inputTd.innerText;

                        let outputText = '';
                        const outputPre = outputTd.querySelector('pre');
                        outputText = outputPre ? outputPre.innerText : outputTd.innerText;

                        if (inputText.length > 0 && !inputText.endsWith('\n')) {
                            inputText += '\n';
                        }

                        outputText = outputText.trim();
                        if(outputText) {
                            outputText = outputText.trimEnd()
                        }

                        result.push({
                            test: inputText,
                            correct_answers: [outputText]
                        });
                    }
                }
            }

            return result;
        } catch (error) {
            console.error('提取vjudge样例失败:', error);
            showToast('❌ 提取失败: ' + error.message);
            return null;
        }
    }

    // 从牛客网提取样例
    async function extractNowcoderSamples() {
        try {
            showToast('⏳ 正在提取牛客样例...');

            // 等待题目区域加载
            await waitForElement('.question-oi-mod', 10000);

            // 查找所有input和output textarea
            const inputAreas = document.querySelectorAll('textarea[data-clipboard-text-id^="input"]');
            const outputAreas = document.querySelectorAll('textarea[data-clipboard-text-id^="output"]');

            console.log('找到inputs:', inputAreas.length, 'outputs:', outputAreas.length);

            if (inputAreas.length === 0) {
                showToast('❌ 未找到输入样例');
                return null;
            }

            const result = [];

            // 牛客网通常是一一对应的
            for (let i = 0; i < inputAreas.length; i++) {
                const inputArea = inputAreas[i];
                const outputArea = outputAreas[i]; // 可能没有对应的output

                let inputText = inputArea.value || inputArea.textContent || '';
                let outputText = outputArea ? (outputArea.value || outputArea.textContent || '') : '';

                // 清理文本
                inputText = inputText.trim();
                outputText = outputText.trim();
                if(outputText) {
                    outputText = outputText.trimEnd()
                }

                // 确保输入末尾有换行
                if (inputText.length > 0 && !inputText.endsWith('\n')) {
                    inputText += '\n';
                }

                result.push({
                    test: inputText,
                    correct_answers: [outputText]
                });
            }

            return result;
        } catch (error) {
            console.error('提取牛客样例失败:', error);
            showToast('❌ 提取失败: ' + error.message);
            return null;
        }
    }

    // 从洛谷提取样例
    async function extractLuoguSamples() {
        try {
            showToast('⏳ 正在提取洛谷样例...');

            // 等待样例容器加载
            await waitForElement('.io-sample', 10000);

            // 洛谷的样例块是成对出现的：奇数索引是输入，偶数索引是输出
            const sampleBlocks = document.querySelectorAll('.io-sample .io-sample-block pre');

            if (sampleBlocks.length === 0) {
                showToast('❌ 未找到样例');
                return null;
            }

            console.log('找到样例块数量:', sampleBlocks.length);

            const result = [];

            // 每两个一组：第1个是输入，第2个是输出
            for (let i = 0; i < sampleBlocks.length; i += 2) {
                if (i + 1 < sampleBlocks.length) {
                    const inputPre = sampleBlocks[i];
                    const outputPre = sampleBlocks[i + 1];

                    let inputText = inputPre.innerText || inputPre.textContent || '';
                    let outputText = outputPre.innerText || outputPre.textContent || '';

                    // 清理文本
                    inputText = inputText.trim();
                    outputText = outputText.trim();
                    if(outputText) {
                        outputText = outputText.trimEnd()
                    }

                    // 确保输入末尾有换行
                    if (inputText.length > 0 && !inputText.endsWith('\n')) {
                        inputText += '\n';
                    }

                    result.push({
                        test: inputText,
                        correct_answers: [outputText]
                    });
                }
            }

            return result;
        } catch (error) {
            console.error('提取洛谷样例失败:', error);
            showToast('❌ 提取失败: ' + error.message);
            return null;
        }
    }

    // 从Codeforces原站提取样例
    function extractCFSamples() {
        const sampleTests = document.querySelector('div.sample-tests');
        if (!sampleTests) {
            showToast('❌ 未找到样例 (div.sample-tests)');
            return null;
        }

        const sampleDiv = sampleTests.querySelector('div.sample-test');
        if (!sampleDiv) {
            showToast('❌ 未找到 div.sample-test');
            return null;
        }

        const inputs = sampleDiv.querySelectorAll('div.input');
        const outputs = sampleDiv.querySelectorAll('div.output');

        if (inputs.length === 0) {
            showToast('❌ 没有找到任何 input 样例');
            return null;
        }

        const result = [];

        for (let i = 0; i < inputs.length; i++) {
            // 提取输入文本
            let inputText = '';
            const inputPre = inputs[i].querySelector('pre');
            if (inputPre) {
                inputText = inputPre.innerText;
            } else {
                inputText = inputs[i].innerText.replace(/^Input\s*/i, '');
            }
            if(inputText) {
                inputText = inputText.trimEnd()
            }

            // 提取输出文本
            let fullOutput = '';
            if (i < outputs.length) {
                const outputPre = outputs[i].querySelector('pre');
                if (outputPre) {
                    fullOutput = outputPre.innerText;
                } else {
                    fullOutput = outputs[i].innerText.replace(/^Output\s*/i, '');
                }
            }
            if(fullOutput) {
                fullOutput = fullOutput.trimEnd()
            }




            // 确保输入末尾有换行
            if (inputText.length > 0 && !inputText.endsWith('\n')) {
                inputText += '\n';
            }

            result.push({
                test: inputText,
                correct_answers: [fullOutput]
            });
        }

        return result;
    }

    // 点击处理函数
    async function handleClick() {
        // 显示加载状态
        btn.classList.add('cf-loading');

        let result;
        const site = getSite();

        try {
            switch (site) {
                case 'cf':
                    result = extractCFSamples();
                    break;
                case 'vjudge':
                    result = await extractVjudgeSamples();
                    break;
                case 'nowcoder':
                    result = await extractNowcoderSamples();
                    break;
                case 'luogu':
                    result = await extractLuoguSamples();
                    break;
                default:
                    showToast('❌ 不支持的网站');
                    result = null;
            }
        } catch (error) {
            console.error('提取失败:', error);
            showToast('❌ 提取过程出错');
            result = null;
        }

        // 移除加载状态
        btn.classList.remove('cf-loading');

        if (!result || result.length === 0) {
            showToast('❌ 没有提取到任何样例');
            return;
        }

        // 转换为JSON字符串，漂亮格式
        const jsonStr = JSON.stringify(result, null, 4);

        // 复制到剪贴板
        try {
            await navigator.clipboard.writeText(jsonStr);
            showToast(`✅ 已复制 ${result.length} 个样例到剪贴板`);
        } catch (err) {
            console.error('复制失败:', err);
            showToast('❌ 复制失败，请检查控制台');
            prompt('手动复制以下内容:', jsonStr);
        }
    }

    // 显示临时提示
    function showToast(msg) {
        const toast = document.createElement('div');
        toast.className = 'cf-toast-message';
        toast.textContent = msg;
        document.body.appendChild(toast);
        setTimeout(() => {
            toast.style.opacity = '0';
            setTimeout(() => toast.remove(), 300);
        }, 2000);
    }

    // 输出欢迎信息
    console.log('样例抓取器已启动 — 支持CF/vjudge/牛客/洛谷，拖动📋按钮，点击复制');
})();