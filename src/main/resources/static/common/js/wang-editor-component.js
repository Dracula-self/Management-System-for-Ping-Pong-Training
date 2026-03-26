/**
 * WangEditor Vue组件
 * 用于非前后端分离项目的富文本编辑器
 * 需要手动引入wang-editor的CSS和JS依赖
 */

// 注册全局Wang-Editor组件
const WangEditorComponent = {
    name: 'WangEditor',
    template: `
        <div class="wang-editor-container" v-if="!initError">
            <!-- 工具栏容器 -->
            <div :id="toolbarId" class="wang-editor-toolbar"></div>
            <!-- 编辑器容器 -->
            <div :id="editorId" class="wang-editor-content" :style="editorStyle"></div>
        </div>
        <div v-else class="wang-editor-error">
            <div class="error-text">{{ initError }}</div>
        </div>
    `,
    props: {
        // 编辑器内容
        modelValue: {
            type: String,
            default: ''
        },
        // 占位符
        placeholder: {
            type: String,
            default: '请输入内容...'
        },
        // 编辑器高度
        height: {
            type: String,
            default: '300px'
        },
        // 是否禁用
        disabled: {
            type: Boolean,
            default: false
        },
        // 上传图片的API地址
        uploadImgServer: {
            type: String,
            default: '/api/file/wang-editor-upload'
        },
        // 上传视频的API地址
        uploadVideoServer: {
            type: String,
            default: '/api/file/wang-editor-upload'
        }
    },
    data() {
        return {
            editor: null,
            toolbar: null,
            isInitialized: false,
            initError: null
        };
    },
    computed: {
        toolbarId() {
            return `wang-editor-toolbar-${this.generateId()}`;
        },
        editorId() {
            return `wang-editor-content-${this.generateId()}`;
        },
        editorStyle() {
            return {
                height: this.height,
                border: '1px solid #ccc',
                borderTop: 'none'
            };
        }
    },
    mounted() {
        this.$nextTick(() => {
            this.initEditor();
        });
    },
    beforeUnmount() {
        this.destroyEditor();
    },
    watch: {
        modelValue: {
            handler(newVal) {
                if (this.editor && this.isInitialized && newVal !== this.editor.getHtml()) {
                    this.editor.setHtml(newVal || '');
                }
            },
            immediate: false
        },
        disabled: {
            handler(newVal) {
                if (this.editor) {
                    if (newVal) {
                        this.editor.disable();
                    } else {
                        this.editor.enable();
                    }
                }
            }
        }
    },
    methods: {
        // 生成唯一ID
        generateId() {
            return this._uid || Math.random().toString(36).substr(2, 9);
        },

        // 初始化编辑器
        initEditor() {
            if (!window.wangEditor) {
                this.initError = 'WangEditor 未加载，请确保已引入 wang-editor 的 CSS 和 JS 文件';
                console.error('WangEditor 未加载，请在页面中引入：');
                console.error('1. <link rel="stylesheet" href="/common/plugins/wang-editor/index.css" />');
                console.error('2. <script src="/common/plugins/wang-editor/index.js"></script>');
                return;
            }

            try {
                const { createEditor, createToolbar } = window.wangEditor;

                // 固定工具栏配置
                const toolbarConfig = {
                    toolbarKeys: [
                        'headerSelect',
                        'bold',
                        'italic',
                        'underline',
                        'through',
                        'code',
                        'clearStyle',
                        '|',
                        'color',
                        'bgColor',
                        '|',
                        'fontSize',
                        'fontFamily',
                        'lineHeight',
                        '|',
                        'bulletedList',
                        'numberedList',
                        '|',
                        'emotion',
                        'insertLink',
                        'uploadImage',
                        'uploadVideo',
                        'insertTable',
                        'codeBlock',
                        'divider',
                        '|',
                        'undo',
                        'redo',
                    ]
                };

                // 编辑器配置
                const editorConfig = {
                    placeholder: this.placeholder,
                    MENU_CONF: {
                        // 上传图片配置
                        uploadImage: {
                            server: this.uploadImgServer,
                            fieldName: 'file',
                            maxFileSize: 10 * 1024 * 1024, // 10M
                            allowedFileTypes: ['image/*'],
                            // 上传成功回调
                            onSuccess: (file, res) => {
                                console.log('图片上传成功', file, res);
                            },
                            // 上传失败回调
                            onFailed: (file, res) => {
                                console.error('图片上传失败', file, res);
                                this.$emit('upload-error', { file, res });
                            },
                            // 上传错误回调
                            onError: (file, err, res) => {
                                console.error('图片上传错误', file, err, res);
                                this.$emit('upload-error', { file, err, res });
                            },
                            // 自定义插入图片
                            customInsert: (res, insertFn) => {
                                // res 是服务端返回的数据
                                // 从 res 中取出图片的 url，然后插入图片
                                if (res && res.errno === 0 && res.data && res.data.url) {
                                    insertFn(res.data.url, res.data.alt || '图片', res.data.href || res.data.url);
                                } else {
                                    console.error('图片上传响应格式错误', res);
                                }
                            }
                        },
                        // 上传视频配置
                        uploadVideo: {
                            server: this.uploadVideoServer,
                            fieldName: 'file',
                            maxFileSize: 50 * 1024 * 1024, // 50M
                            allowedFileTypes: ['video/*'],
                            // 上传成功回调
                            onSuccess: (file, res) => {
                                console.log('视频上传成功', file, res);
                                this.$emit('video-upload-success', { file, res });
                            },
                            // 上传失败回调
                            onFailed: (file, res) => {
                                console.error('视频上传失败', file, res);
                                this.$emit('video-upload-error', { file, res });
                            },
                            // 上传错误回调
                            onError: (file, err, res) => {
                                console.error('视频上传错误', file, err, res);
                                this.$emit('video-upload-error', { file, err, res });
                            },
                            // 自定义插入视频
                            customInsert: (res, insertFn) => {
                                // res 是服务端返回的数据
                                // 从 res 中取出视频的 url，然后插入视频
                                if (res && res.errno === 0 && res.data && res.data.url) {
                                    insertFn(res.data.url);
                                } else {
                                    console.error('视频上传响应格式错误', res);
                                }
                            }
                        }
                    }
                };

                // 创建编辑器
                this.editor = createEditor({
                    selector: `#${this.editorId}`,
                    config: editorConfig
                });

                // 创建工具栏
                this.toolbar = createToolbar({
                    editor: this.editor,
                    selector: `#${this.toolbarId}`,
                    config: toolbarConfig
                });

                // 设置初始内容
                if (this.modelValue) {
                    this.editor.setHtml(this.modelValue);
                }

                // 监听内容变化
                this.editor.on('change', () => {
                    const html = this.editor.getHtml();
                    this.$emit('update:modelValue', html);
                    this.$emit('change', html);
                });

                // 监听焦点事件
                this.editor.on('focus', () => {
                    this.$emit('focus');
                });

                this.editor.on('blur', () => {
                    this.$emit('blur');
                });

                // 设置禁用状态
                if (this.disabled) {
                    this.editor.disable();
                }

                this.isInitialized = true;
                this.$emit('ready', this.editor);

            } catch (error) {
                this.initError = '编辑器初始化失败: ' + error.message;
                console.error('WangEditor 初始化失败', error);
                this.$emit('init-error', error);
            }
        },

        // 销毁编辑器
        destroyEditor() {
            if (this.toolbar) {
                this.toolbar.destroy();
                this.toolbar = null;
            }
            if (this.editor) {
                this.editor.destroy();
                this.editor = null;
            }
            this.isInitialized = false;
        },

        // 获取编辑器内容 - HTML格式
        getHtml() {
            return this.editor ? this.editor.getHtml() : '';
        },

        // 获取编辑器内容 - 纯文本
        getText() {
            return this.editor ? this.editor.getText() : '';
        },

        // 设置编辑器内容
        setHtml(html) {
            if (this.editor) {
                this.editor.setHtml(html || '');
            }
        },

        // 插入文本
        insertText(text) {
            if (this.editor) {
                this.editor.insertText(text);
            }
        },

        // 清空内容
        clear() {
            if (this.editor) {
                this.editor.clear();
            }
        },

        // 聚焦
        focus() {
            if (this.editor) {
                this.editor.focus();
            }
        },

        // 失焦
        blur() {
            if (this.editor) {
                this.editor.blur();
            }
        },

        // 启用编辑器
        enable() {
            if (this.editor) {
                this.editor.enable();
            }
        },

        // 禁用编辑器
        disable() {
            if (this.editor) {
                this.editor.disable();
            }
        }
    }
};

// 添加组件样式
const componentStyle = document.createElement('style');
componentStyle.textContent = `
/* WangEditor 组件样式 */
.wang-editor-container {
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    overflow: hidden;
}

.wang-editor-toolbar {
    border-bottom: 1px solid #dcdfe6;
}

.wang-editor-error {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 200px;
    background: #fef0f0;
    border: 1px solid #fbc4c4;
    border-radius: 4px;
}

.wang-editor-error .error-text {
    color: #f56c6c;
    font-size: 14px;
    text-align: center;
    padding: 20px;
}
`;
document.head.appendChild(componentStyle);

// 全局注册组件
if (window.Vue && window.Vue.createApp) {
    // Vue 3
    window.WangEditorComponent = WangEditorComponent;
} else if (window.Vue) {
    // Vue 2
    Vue.component('WangEditor', WangEditorComponent);
    window.WangEditorComponent = WangEditorComponent;
} 