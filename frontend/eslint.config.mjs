import next from '@next/eslint-plugin-next';
import prettierConfig from 'eslint-config-prettier';
import prettierPlugin from 'eslint-plugin-prettier';
import react from 'eslint-plugin-react';
import * as espree from 'espree';
import globals from 'globals';

export default [
  {
    ignores: ['node_modules', 'dist', 'public', '.next', 'out']
  },
  {
    files: ['**/*.js', '**/*.jsx'],
    languageOptions: {
      ecmaVersion: 'latest',
      sourceType: 'module',
      globals: {
        ...globals.browser
      },
      parser: espree,
      parserOptions: { ecmaFeatures: { jsx: true } }
    },
    plugins: {
      react,
      next,
      prettier: prettierPlugin
    },
    settings: {
      react: {
        version: 'detect'
      }
    },
    rules: {
      'prettier/prettier': 'error',
      'react/react-in-jsx-scope': 'off',
      'react/jsx-filename-extension': [1, { extensions: ['.js', '.jsx'] }]
    }
  },
  prettierConfig
];
