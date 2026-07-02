import { defineConfig } from 'vitepress'

export default defineConfig({
  base: '/EligiusConnector/',
  title: "EligiusConnector",
  srcDir: 'src',
  sitemap: {
    hostname: 'https://eligiusmc.github.io/EligiusConnector/'
  },
  transformHead({ siteConfig, pageData }) {
    const head = []
    const locales = Object.keys(siteConfig.site.locales)
    for (const locale of locales) {
      if (locale === 'root') continue
      head.push(['link', { rel: 'alternate', hreflang: locale, href: `https://eligiusmc.github.io/EligiusConnector/${locale}/${pageData.relativePath.replace('index.md', '').replace('.md', '')}` }])
    }
    head.push(['link', { rel: 'alternate', hreflang: 'x-default', href: `https://eligiusmc.github.io/EligiusConnector/${pageData.relativePath.replace('index.md', '').replace('.md', '')}` }])
    return head
  },
  head: [
    ['link', { rel: 'icon', href: '/EligiusConnector/assets/angry.png' }],
    ['script', {}, `
      (function() {
        if (typeof window === 'undefined') return;
        if (localStorage.getItem('lang-redirect-done')) return;
        var lang = navigator.language || navigator.userLanguage;
        var prefix = lang.split('-')[0];
        var supported = ['es', 'fr', 'de', 'pt', 'ru'];
        var currentPath = window.location.pathname;
        if (supported.includes(prefix) && (currentPath === '/EligiusConnector/' || currentPath === '/EligiusConnector/index.html')) {
          localStorage.setItem('lang-redirect-done', 'true');
          window.location.href = '/EligiusConnector/' + prefix + '/';
        }
      })();
    `]
  ],
  locales: {
    root: {
      label: 'English',
      lang: 'en',
      description: "Official documentation for the ultimate Discord-Minecraft bridge plugin.",
      themeConfig: {
        nav: [
          { text: '🏠 Home', link: '/' },
          { text: '📖 Documentation', link: '/installation' }
        ],
        sidebar: [
          { text: '🚀 Getting Started', items: [
            { text: 'Installation', link: '/installation' },
            { text: 'Commands & Permissions', link: '/commands' }
          ]},
          { text: '⚙️ Module Configuration', items: [
            { text: 'Bot & Database (config.yml)', link: '/config/bot' },
            { text: 'Chat Bridge (chat.yml)', link: '/config/chat' },
            { text: 'Player Events (player_events.yml)', link: '/config/player-events' },
            { text: 'Verification (verify.yml)', link: '/config/verify' },
            { text: 'Profile Panels (profile.yml)', link: '/config/profile' },
            { text: 'Server Status (status.yml)', link: '/config/status' },
            { text: 'Dynamic Counters (counters.yml)', link: '/config/counters' },
            { text: 'Synchronization (synchronization.yml)', link: '/config/synchronization' },
            { text: 'Console Relay (console.yml)', link: '/config/console' }
          ]},
          { text: '🛡️ Support', items: [
            { text: 'Troubleshooting', link: '/troubleshooting' }
          ]}
        ]
      }
    },
    es: {
      label: 'Español', lang: 'es',
      description: "Documentación oficial del plugin puente Discord-Minecraft.",
      themeConfig: {
        nav: [
          { text: '🏠 Inicio', link: '/es/' },
          { text: '📖 Documentación', link: '/es/installation' }
        ],
        sidebar: [
          { text: '🚀 Primeros Pasos', items: [
            { text: 'Instalación', link: '/es/installation' },
            { text: 'Comandos y Permisos', link: '/es/commands' }
          ]},
          { text: '⚙️ Configuración Modular', items: [
            { text: 'Bot y DB (config.yml)', link: '/es/config/bot' },
            { text: 'Chat Bridge (chat.yml)', link: '/es/config/chat' },
            { text: 'Eventos (player_events.yml)', link: '/es/config/player-events' },
            { text: 'Verificación (verify.yml)', link: '/es/config/verify' },
            { text: 'Paneles (profile.yml)', link: '/es/config/profile' },
            { text: 'Estado (status.yml)', link: '/es/config/status' },
            { text: 'Contadores (counters.yml)', link: '/es/config/counters' },
            { text: 'Sincronización (synchronization.yml)', link: '/es/config/synchronization' },
            { text: 'Consola (console.yml)', link: '/es/config/console' }
          ]},
          { text: '🛡️ Soporte', items: [
            { text: 'Troubleshooting', link: '/es/troubleshooting' }
          ]}
        ]
      }
    },
    fr: { label: 'Français', lang: 'fr', themeConfig: { nav: [
      { text: '🏠 Accueil', link: '/fr/' }, { text: '📖 Documentation', link: '/fr/installation' }
    ], sidebar: [
      { text: '🚀 Démarrage', items: [{ text: 'Installation', link: '/fr/installation' }, { text: 'Commandes', link: '/fr/commands' }] },
      { text: '⚙️ Configuration', items: [
        { text: 'Bot (config.yml)', link: '/fr/config/bot' }, { text: 'Chat', link: '/fr/config/chat' },
        { text: 'Événements', link: '/fr/config/player-events' }, { text: 'Vérification', link: '/fr/config/verify' },
        { text: 'Profils', link: '/fr/config/profile' }, { text: 'Statut', link: '/fr/config/status' },
        { text: 'Compteurs', link: '/fr/config/counters' }, { text: 'Synchronisation', link: '/fr/config/synchronization' }
      ]},
      { text: '🛡️ Support', items: [{ text: 'Dépannage', link: '/fr/troubleshooting' }] }
    ] } },
    de: { label: 'Deutsch', lang: 'de', themeConfig: { nav: [
      { text: '🏠 Start', link: '/de/' }, { text: '📖 Dokumentation', link: '/de/installation' }
    ], sidebar: [
      { text: '🚀 Erste Schritte', items: [{ text: 'Installation', link: '/de/installation' }, { text: 'Befehle', link: '/de/commands' }] },
      { text: '⚙️ Konfiguration', items: [
        { text: 'Bot', link: '/de/config/bot' }, { text: 'Chat', link: '/de/config/chat' },
        { text: 'Ereignisse', link: '/de/config/player-events' }, { text: 'Verifizierung', link: '/de/config/verify' },
        { text: 'Profile', link: '/de/config/profile' }, { text: 'Status', link: '/de/config/status' },
        { text: 'Zähler', link: '/de/config/counters' }, { text: 'Synchronisation', link: '/de/config/synchronization' }
      ]},
      { text: '🛡️ Support', items: [{ text: 'Fehlerbehebung', link: '/de/troubleshooting' }] }
    ] } },
    pt: { label: 'Português', lang: 'pt', themeConfig: { nav: [
      { text: '🏠 Início', link: '/pt/' }, { text: '📖 Documentação', link: '/pt/installation' }
    ], sidebar: [
      { text: '🚀 Primeiros Passos', items: [{ text: 'Instalação', link: '/pt/installation' }, { text: 'Comandos', link: '/pt/commands' }] },
      { text: '⚙️ Configuração', items: [
        { text: 'Bot', link: '/pt/config/bot' }, { text: 'Chat', link: '/pt/config/chat' },
        { text: 'Eventos', link: '/pt/config/player-events' }, { text: 'Verificação', link: '/pt/config/verify' },
        { text: 'Perfis', link: '/pt/config/profile' }, { text: 'Status', link: '/pt/config/status' },
        { text: 'Contadores', link: '/pt/config/counters' }, { text: 'Sincronização', link: '/pt/config/synchronization' }
      ]},
      { text: '🛡️ Suporte', items: [{ text: 'Troubleshooting', link: '/pt/troubleshooting' }] }
    ] } },
    ru: { label: 'Русский', lang: 'ru', themeConfig: { nav: [
      { text: '🏠 Главная', link: '/ru/' }, { text: '📖 Документация', link: '/ru/installation' }
    ], sidebar: [
      { text: '🚀 Начало', items: [{ text: 'Установка', link: '/ru/installation' }, { text: 'Команды', link: '/ru/commands' }] },
      { text: '⚙️ Конфигурация', items: [
        { text: 'Бот', link: '/ru/config/bot' }, { text: 'Чат', link: '/ru/config/chat' },
        { text: 'События', link: '/ru/config/player-events' }, { text: 'Верификация', link: '/ru/config/verify' },
        { text: 'Профили', link: '/ru/config/profile' }, { text: 'Статус', link: '/ru/config/status' },
        { text: 'Счётчики', link: '/ru/config/counters' }, { text: 'Синхронизация', link: '/ru/config/synchronization' }
      ]},
      { text: '🛡️ Поддержка', items: [{ text: 'Ошибки', link: '/ru/troubleshooting' }] }
    ] } }
  },
  themeConfig: {
    socialLinks: [
      { icon: 'github', link: 'https://github.com/Eligiusmc/EligiusConnector' }
    ],
    footer: {
      message: 'Released under the MIT License.',
      copyright: 'Copyright © 2026 Eligius MC'
    }
  }
})
