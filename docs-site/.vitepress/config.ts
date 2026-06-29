import { defineConfig } from 'vitepress'

export default defineConfig({
  title: 'EligiusConnector',
  description: 'The ultimate Discord-Minecraft bridge plugin',
  
  themeConfig: {
    nav: [
      { text: 'Home', link: '/' },
      { text: 'Guide', link: '/guide/' },
      { text: 'Config', link: '/config/' },
      { text: 'Wiki', items: [
        { text: 'English', link: '/en/' },
        { text: 'Español', link: '/es/' }
      ]},
      { text: 'GitHub', link: 'https://github.com/Eligiusmc/EligiusConnector' }
    ],
    
    sidebar: {
      '/guide/': [
        {
          text: 'Guide',
          items: [
            { text: 'Introduction', link: '/guide/' },
            { text: 'Getting Started', link: '/guide/getting-started' },
            { text: 'Configuration', link: '/guide/configuration' },
            { text: 'Commands', link: '/guide/commands' },
            { text: 'Permissions', link: '/guide/permissions' },
            { text: 'Placeholders', link: '/guide/placeholders' }
          ]
        }
      ],
      '/config/': [
        {
          text: 'Configuration',
          items: [
            { text: 'Overview', link: '/config/' },
            { text: 'Main Config', link: '/config/main' },
            { text: 'Chat', link: '/config/chat' },
            { text: 'Synchronization', link: '/config/synchronization' },
            { text: 'Notifications', link: '/config/notifications' },
            { text: 'Events', link: '/config/events' }
          ]
        }
      ]
    },
    
    socialLinks: [
      { icon: 'github', link: 'https://github.com/Eligiusmc/EligiusConnector' },
      { icon: 'discord', link: 'https://discord.gg/eligius' }
    ],
    
    footer: {
      message: 'Released under the MIT License.',
      copyright: 'Copyright © 2026 Eligiusmc'
    }
  },
  
  locales: {
    root: {
      label: 'English',
      lang: 'en'
    },
    es: {
      label: 'Español',
      lang: 'es',
      themeConfig: {
        nav: [
          { text: 'Inicio', link: '/es/' },
          { text: 'Guía', link: '/es/guia/' },
          { text: 'Config', link: '/es/config/' },
          { text: 'GitHub', link: 'https://github.com/Eligiusmc/EligiusConnector' }
        ]
      }
    }
  }
})
